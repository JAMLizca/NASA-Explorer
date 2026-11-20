/** Contiene la implementación relacionada con esta sección de la aplicación. */
package com.example.nasaexplorer.presentation.neows

import com.example.nasaexplorer.data.remote.neows.AsteroidDto

/** Encapsula la responsabilidad principal de esta sección. */
data class AsteroidStatistics(
    val total: Int,
    val potentiallyHazardous: Int,
    val averageDiameter: Double
)

/** Implementa la operación principal de esta sección. */
fun calculateAsteroidStatistics(
    asteroids: List<AsteroidDto>
): AsteroidStatistics {

    val validDiameters = asteroids.mapNotNull {
        it.estimatedDiameter?.meters?.maximum
    }.filter { it > 0.0 }

    val average = if (validDiameters.isNotEmpty()) {
        validDiameters.average()
    } else {
        0.0
    }

    return AsteroidStatistics(
        total = asteroids.size,
        potentiallyHazardous = asteroids.count {
            it.isPotentiallyHazardous == true
        },
        averageDiameter = average
    )
}
