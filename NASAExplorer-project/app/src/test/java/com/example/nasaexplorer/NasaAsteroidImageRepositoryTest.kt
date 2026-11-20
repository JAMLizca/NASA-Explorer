/** Prueba el comportamiento de la funcionalidad correspondiente y verifica sus resultados esperados. */
package com.example.nasaexplorer

import com.example.nasaexplorer.data.remote.images.NasaImageItem
import com.example.nasaexplorer.data.remote.images.NasaImageLink
import com.example.nasaexplorer.data.remote.images.NasaImageMetadata
import com.example.nasaexplorer.data.remote.images.NasaImageCollection
import com.example.nasaexplorer.data.remote.images.NasaImageSearchResponse
import com.example.nasaexplorer.data.repository.selectVerifiedAsteroidPhoto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Agrupa las pruebas relacionadas con esta funcionalidad. */
class NasaAsteroidImageRepositoryTest {
    /** Verifica el resultado esperado de este escenario. */
    @Test
    fun selectsRealAsteroidPhotoWithMatchingTitle() {
        val result = selectVerifiedAsteroidPhoto(
            asteroidName = "433 Eros",
            response = response(
                item(
                    title = "Eros in Stereo",
                    imageUrl = "https://images-assets.nasa.gov/image/PIA02471/PIA02471~thumb.jpg"
                )
            )
        )

        assertEquals("Eros in Stereo", result?.title)
        assertEquals("PIA02471", result?.nasaId)
    }

    /** Verifica el resultado esperado de este escenario. */
    @Test
    fun doesNotUseConceptArtEvenWhenAsteroidNameMatches() {
        val result = selectVerifiedAsteroidPhoto(
            asteroidName = "Eros",
            response = response(
                item(
                    title = "NASA NEAR Spacecraft Rendezvous with Asteroid Eros Artist Concept",
                    imageUrl = "https://images-assets.nasa.gov/image/PIA18177/PIA18177~medium.jpg"
                )
            )
        )

        assertNull(result)
    }

    /** Verifica el resultado esperado de este escenario. */
    @Test
    fun doesNotUseImageOfSampleOrSpacecraftAsAsteroidPhoto() {
        val result = selectVerifiedAsteroidPhoto(
            asteroidName = "Bennu",
            response = response(
                item(
                    title = "Vial with Sample of Asteroid Bennu",
                    imageUrl = "https://images-assets.nasa.gov/image/Bennu_Sample2/Bennu_Sample2~medium.jpg"
                )
            )
        )

        assertNull(result)
    }

    /** Verifica el resultado esperado de este escenario. */
    @Test
    fun doesNotTrustDescriptionWhenImageTitleDoesNotIdentifyAsteroid() {
        val result = selectVerifiedAsteroidPhoto(
            asteroidName = "433 Eros",
            response = response(
                item(
                    title = "Earth Science",
                    description = "The spacecraft will rendezvous with asteroid 433 Eros.",
                    imageUrl = "https://images-assets.nasa.gov/image/9701595/9701595~small.jpg"
                )
            )
        )

        assertNull(result)
    }

    private fun response(vararg items: NasaImageItem) =
        NasaImageSearchResponse(
            collection = NasaImageCollection(items = items.toList())
        )

    private fun item(
        title: String,
        imageUrl: String,
        description: String = ""
    ) = NasaImageItem(
        data = listOf(
            NasaImageMetadata(
                nasaId = imageUrl.substringAfterLast('/').substringBefore('~'),
                title = title,
                description = description
            )
        ),
        links = listOf(
            NasaImageLink(
                href = imageUrl,
                render = "image"
            )
        )
    )
}
