/** Verifica la conversión de geometrías EONET a ubicaciones aproximadas. */
package com.example.nasaexplorer

import com.example.nasaexplorer.data.remote.eonet.EonetEventDto
import com.example.nasaexplorer.data.remote.eonet.EonetGeometryDto
import com.example.nasaexplorer.presentation.eonet.toMapPoints
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EonetMapPointTest {
    @Test
    fun pointGeometryUsesLongitudeAndLatitudeInMapOrder() {
        val event = eventWithCoordinates("[-70.5, 4.25]")

        val point = listOf(event).toMapPoints().single()

        assertEquals(4.25, point.latitude, 0.0)
        assertEquals(-70.5, point.longitude, 0.0)
    }

    @Test
    fun polygonGeometryUsesApproximateCenterOfItsCoordinates() {
        val event = eventWithCoordinates("[[[-72, 2], [-68, 2], [-68, 6], [-72, 6], [-72, 2]]]")

        val point = listOf(event).toMapPoints().single()

        assertEquals(4.0, point.latitude, 0.0001)
        assertEquals(-70.0, point.longitude, 0.0001)
    }

    @Test
    fun eventWithoutValidCoordinatesIsNotMapped() {
        val event = eventWithCoordinates("[200, 95]")

        assertTrue(listOf(event).toMapPoints().isEmpty())
    }

    @Test
    fun mapPointKeepsEventDescriptionAndCategoryType() {
        val event = eventWithCoordinates("[-70.5, 4.25]").copy(
            description = "Near the southern coast.",
            categories = listOf(
                com.example.nasaexplorer.data.remote.eonet.EonetCategoryDto(
                    id = "wildfires",
                    title = "Wildfires"
                )
            )
        )

        val point = listOf(event).toMapPoints().single()

        assertEquals("Near the southern coast.", point.description)
        assertEquals("wildfires", point.categoryId)
        assertEquals(
            "Incendios en zonas naturales, bosques o llanuras; pueden tener origen natural o humano.",
            point.categoryDescription
        )
    }

    private fun eventWithCoordinates(coordinates: String) = EonetEventDto(
        id = "event-1",
        title = "Test event",
        geometry = listOf(
            EonetGeometryDto(
                date = "2026-11-17T00:00:00Z",
                coordinates = JsonParser.parseString(coordinates)
            )
        )
    )
}
