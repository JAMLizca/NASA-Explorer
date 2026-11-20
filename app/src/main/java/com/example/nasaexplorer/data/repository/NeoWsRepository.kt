/** Orquesta las fuentes de datos y ofrece una interfaz coherente para la presentación. */
package com.example.nasaexplorer.data.repository

import com.example.nasaexplorer.data.remote.neows.NeoWsDto
import com.example.nasaexplorer.data.remote.neows.NeoWsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/** Define las operaciones disponibles en el servicio HTTP remoto. */
class NeoWsApiException(
    val statusCode: Int,
    val title: String,
    override val message: String,
    cause: Throwable
) : IOException(message, cause)

/** Centraliza el acceso y manejo de datos para neo ws. */
class NeoWsRepository(
    private val api: NeoWsApi
) {

    suspend fun getAsteroids(
        startDate: String,
        endDate: String,
        apiKey: String
    ): Result<NeoWsDto> = withContext(Dispatchers.IO) {

        try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
                isLenient = false
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val start = parser.parse(startDate)
                ?: throw IllegalArgumentException("La fecha inicial no es válida.")
            val end = parser.parse(endDate)
                ?: throw IllegalArgumentException("La fecha final no es válida.")
            // Rechaza fechas lenient como 2026-02-31 que SimpleDateFormat podría normalizar.
            require(parser.format(start) == startDate && parser.format(end) == endDate) {
                "Las fechas deben tener el formato AAAA-MM-DD."
            }
            require(!end.before(start)) {
                "La fecha final debe ser igual o posterior a la fecha inicial."
            }
            // El rango se cuenta inclusivamente: diferencia de 6 días equivale a 7 fechas consultadas.
            require((end.time - start.time) / MILLIS_PER_DAY < MAX_FEED_DAYS) {
                "NeoWs permite consultar un máximo de 7 días por solicitud."
            }
            require(!end.after(parser.parse(parser.format(Date())))) {
                "La fecha final no puede estar en el futuro."
            }
            val response = api.getAsteroids(
                startDate = startDate,
                endDate = endDate,
                apiKey = apiKey
            )

            Result.success(response)

        } catch (e: IOException) {
            Result.failure(IOException("Error de conexión. Verifica tu internet.", e))

        } catch (e: HttpException) {
            val (title, message) = when (e.code()) {
                429 -> "Límite de solicitudes alcanzado" to
                    "NASA limitó temporalmente las consultas. Espera un momento y vuelve a intentar. " +
                    "DEMO_KEY tiene una cuota reducida; configura tu propia clave NASA en local.properties."
                403 -> "NASA no aceptó la clave API" to
                    "Revisa que NASA_API_KEY en local.properties sea válida y vuelve a compilar la app."
                400 -> "NASA rechazó el periodo" to
                    "Comprueba que las fechas sean válidas y que el periodo no supere los 7 días."
                else -> "Servicio NASA no disponible" to
                    "NASA respondió con el código HTTP ${e.code()}. Intenta nuevamente más tarde."
            }
            Result.failure(NeoWsApiException(e.code(), title, message, e))

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private companion object {
        const val MAX_FEED_DAYS = 7
        const val MILLIS_PER_DAY = 24 * 60 * 60 * 1000L
    }
}
