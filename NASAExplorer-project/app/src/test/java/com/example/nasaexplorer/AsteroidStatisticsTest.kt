/** Prueba el comportamiento de la funcionalidad correspondiente y verifica sus resultados esperados. */
package com.example.nasaexplorer

import com.example.nasaexplorer.data.remote.neows.AsteroidDto
import com.example.nasaexplorer.data.remote.neows.DiameterRangeDto
import com.example.nasaexplorer.data.remote.neows.EstimatedDiameterDto
import com.example.nasaexplorer.presentation.neows.calculateAsteroidStatistics
import org.junit.Assert.assertEquals
import org.junit.Test

/** Agrupa las pruebas relacionadas con esta funcionalidad. */
class AsteroidStatisticsTest {

    /** Verifica el resultado esperado de este escenario. */
    @Test
    fun testCalculateAsteroidStatistics() {
        val asteroids = listOf(
            AsteroidDto(
                id = "1",
                name = "Asteroid A",
                estimatedDiameter = EstimatedDiameterDto(meters = DiameterRangeDto(minimum = 10.0, maximum = 20.0)),
                isPotentiallyHazardous = true
            ),
            AsteroidDto(
                id = "2",
                name = "Asteroid B",
                estimatedDiameter = EstimatedDiameterDto(meters = DiameterRangeDto(minimum = 30.0, maximum = 40.0)),
                isPotentiallyHazardous = false
            )
        )

        val stats = calculateAsteroidStatistics(asteroids)

        assertEquals(2, stats.total)
        assertEquals(1, stats.potentiallyHazardous)
        assertEquals(30.0, stats.averageDiameter, 0.01)
    }

    /** Verifica el resultado esperado de este escenario. */
    @Test
    fun testEmptyAsteroidStatistics() {
        val stats = calculateAsteroidStatistics(emptyList())

        assertEquals(0, stats.total)
        assertEquals(0, stats.potentiallyHazardous)
        assertEquals(0.0, stats.averageDiameter, 0.001)
    }
}
