/** Prueba el comportamiento relacionado con asteroid analytics. */
package com.example.nasaexplorer

import com.example.nasaexplorer.data.remote.neows.AsteroidDto
import com.example.nasaexplorer.data.remote.neows.CloseApproachDto
import com.example.nasaexplorer.data.remote.neows.DiameterRangeDto
import com.example.nasaexplorer.data.remote.neows.EstimatedDiameterDto
import com.example.nasaexplorer.data.remote.neows.MissDistanceDto
import com.example.nasaexplorer.data.remote.neows.RelativeVelocityDto
import com.example.nasaexplorer.domain.model.AsteroidSortOption
import com.example.nasaexplorer.domain.model.calculateAsteroidStatistics
import com.example.nasaexplorer.domain.model.filterAndSortAsteroids
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Grupo de pruebas relacionadas con asteroid analytics. */
class AsteroidAnalyticsTest {
    private val objects = listOf(
        asteroid("2", "Beta", 20.0, 9.0, 40.0, hazardous = true),
        asteroid("1", "Alpha", 10.0, 11.0, 25.0, hazardous = false)
    )

    @Test
    /** Verifica el comportamiento esperado de filters by name and hazard classification. */
    fun filtersByNameAndHazardClassification() {
        val result = filterAndSortAsteroids(objects, "alpha", true, AsteroidSortOption.NAME)
        assertTrue(result.isEmpty())
        assertEquals(
            listOf("2"),
            filterAndSortAsteroids(objects, "", true, AsteroidSortOption.NAME).map { it.id }
        )
    }

    @Test
    /** Verifica el comportamiento esperado de sorts using n a s a provided measurements. */
    fun sortsUsingNASAProvidedMeasurements() {
        assertEquals(
            listOf("1", "2"),
            filterAndSortAsteroids(objects, "", false, AsteroidSortOption.NAME).map { it.id }
        )
        assertEquals(
            listOf("1", "2"),
            filterAndSortAsteroids(objects, "", false, AsteroidSortOption.SPEED).map { it.id }
        )
        assertEquals(
            listOf("1", "2"),
            filterAndSortAsteroids(objects, "", false, AsteroidSortOption.DISTANCE).map { it.id }
        )
        assertEquals(
            listOf("2", "1"),
            filterAndSortAsteroids(objects, "", false, AsteroidSortOption.DIAMETER).map { it.id }
        )
    }

    @Test
    /** Verifica el comportamiento esperado de calculates counts and largest known diameter. */
    fun calculatesCountsAndLargestKnownDiameter() {
        val stats = calculateAsteroidStatistics(objects)
        assertEquals(2, stats.count)
        assertEquals(1, stats.hazardousCount)
        assertEquals(20.0, stats.largestDiameterMeters!!, 0.0)
    }

    @Test
    /** Verifica el comportamiento esperado de absent measurements are not invented. */
    fun absentMeasurementsAreNotInvented() {
        val stats = calculateAsteroidStatistics(listOf(AsteroidDto(id = "3")))
        assertEquals(1, stats.count)
        assertEquals(0, stats.hazardousCount)
        assertEquals(null, stats.largestDiameterMeters)
    }

    private fun asteroid(
        id: String,
        name: String,
        diameter: Double,
        speed: Double,
        distance: Double,
        hazardous: Boolean
    ) = AsteroidDto(
        id = id,
        name = name,
        estimatedDiameter = EstimatedDiameterDto(
            meters = DiameterRangeDto(minimum = diameter / 2, maximum = diameter)
        ),
        isPotentiallyHazardous = hazardous,
        closeApproachData = listOf(
            CloseApproachDto(
                relativeVelocity = RelativeVelocityDto(speed.toString()),
                missDistance = MissDistanceDto(distance.toString())
            )
        )
    )
}
