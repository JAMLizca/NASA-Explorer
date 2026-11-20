/** Orquesta las fuentes de datos y ofrece una interfaz coherente para la presentación. */
package com.example.nasaexplorer.data.repository

import com.example.nasaexplorer.data.remote.images.NasaAsteroidPhoto
import com.example.nasaexplorer.data.remote.images.NasaImageApi
import com.example.nasaexplorer.data.remote.images.NasaImageItem
import com.example.nasaexplorer.data.remote.images.NasaImageSearchResponse
import com.google.gson.JsonParseException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.Normalizer
import java.util.Locale
import retrofit2.HttpException

/** Centraliza el acceso y manejo de datos para nasa asteroid image. */
class NasaAsteroidImageRepository(
    private val api: NasaImageApi
) {
    suspend fun findVerifiedPhoto(asteroidName: String): Result<NasaAsteroidPhoto?> =
        withContext(Dispatchers.IO) {
            try {
                val queryName = asteroidName.trim()
                require(queryName.isNotEmpty()) { "El nombre del asteroide no puede estar vacío." }
                // Los resultados NeoWs suelen anteponer el número de catálogo al nombre propio.
                val searchName = queryName.replace(Regex("^\\(?\\d+\\)?\\s+"), "")
                    .takeIf { normalizeForMatch(it).length >= MIN_NAME_LENGTH }
                    ?: queryName
                val response = api.searchImages(query = "asteroid $searchName")
                Result.success(selectVerifiedAsteroidPhoto(queryName, response))
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                Result.failure(e)
            } catch (e: HttpException) {
                Result.failure(e)
            } catch (e: JsonParseException) {
                Result.failure(e)
            }
        }
}

internal fun selectVerifiedAsteroidPhoto(
    asteroidName: String,
    response: NasaImageSearchResponse
): NasaAsteroidPhoto? {
    val candidates = asteroidNameCandidates(asteroidName)
    if (candidates.isEmpty()) return null

    return response.collection?.items.orEmpty()
        .mapNotNull { item -> item.toVerifiedPhoto(candidates) }
        .firstOrNull()
}

private fun NasaImageItem.toVerifiedPhoto(
    asteroidNameCandidates: List<String>
): NasaAsteroidPhoto? {
    val metadata = data.firstOrNull() ?: return null
    val title = metadata.title?.takeIf(String::isNotBlank) ?: return null
    val normalizedTitle = normalizeForMatch(title)
    // Es preferible no mostrar imagen a presentar una ilustración o una foto sin coincidencia clara.
    if (NON_PHOTOGRAPHIC_TITLE_TERMS.any { term -> term in normalizedTitle }) return null
    if (asteroidNameCandidates.none { candidate -> normalizedTitle.containsPhrase(candidate) }) {
        return null
    }

    val imageUrl = links
        .firstOrNull {
            it.rel == "canonical" &&
                it.render == "image" &&
                it.href.orEmpty().startsWith("https://")
        }
        ?.href
        ?: links.firstOrNull {
            it.render == "image" && it.href.orEmpty().startsWith("https://")
        }?.href
        ?: return null
    val nasaId = metadata.nasaId?.takeIf(String::isNotBlank) ?: return null

    return NasaAsteroidPhoto(
        imageUrl = imageUrl,
        title = title,
        nasaId = nasaId
    )
}

private fun asteroidNameCandidates(name: String): List<String> {
    val normalizedName = normalizeForMatch(name)
    if (normalizedName.isBlank()) return emptyList()

    val candidates = linkedSetOf(normalizedName)
    val withoutLeadingNumber = normalizedName.replaceFirst(Regex("^\\d+\\s+"), "").trim()
    if (withoutLeadingNumber.length >= MIN_NAME_LENGTH) {
        candidates += withoutLeadingNumber
    }
    return candidates.sortedByDescending(String::length)
}

private fun normalizeForMatch(value: String): String =
    Normalizer.normalize(value, Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "")
        .lowercase(Locale.ROOT)
        .replace(Regex("[^a-z0-9]+"), " ")
        .trim()

private fun String.containsPhrase(phrase: String): Boolean =
    " $this ".contains(" $phrase ")

private const val MIN_NAME_LENGTH = 3

private val NON_PHOTOGRAPHIC_TITLE_TERMS = listOf(
    "artist concept",
    "artist rendering",
    "illustration",
    "lithograph",
    "rendering",
    "render",
    "simulation",
    "model",
    "animation",
    "diagram",
    "infographic",
    "artwork",
    "poster",
    "spacecraft",
    "sample",
    "vial",
    "researcher",
    "team photo",
    "launch",
    "capsule",
    "laboratory"
)
