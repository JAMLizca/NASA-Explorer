/** Contiene el modelo o cálculos de dominio usados por las estadísticas. */
package com.example.nasaexplorer.domain.model

import com.example.nasaexplorer.data.remote.neows.AsteroidDto

/** Encapsula la responsabilidad principal de asteroid sort option. */
enum class AsteroidSortOption {
    NAME,
    DIAMETER,
    SPEED,
    DISTANCE
}

/** Encapsula la responsabilidad principal de asteroid statistics. */
data class AsteroidStatistics(
    val count: Int,
    val hazardousCount: Int,
    val largestDiameterMeters: Double?
)

/** Implementa el comportamiento asociado a filter and sort asteroids. */
fun filterAndSortAsteroids(
    asteroids: List<AsteroidDto>,
    query: String,
    onlyHazardous: Boolean,
    sortOption: AsteroidSortOption
): List<AsteroidDto> {
    val filtered = asteroids.filter { asteroid ->
        asteroid.name.orEmpty().contains(query.trim(), ignoreCase = true) &&
            (!onlyHazardous || asteroid.isPotentiallyHazardous == true)
    }

    return when (sortOption) {
        AsteroidSortOption.NAME -> filtered.sortedBy { it.name.orEmpty().lowercase() }
        AsteroidSortOption.DIAMETER -> filtered.sortedByDescending {
            it.estimatedDiameter?.meters?.maximum
        }
        // NeoWs suele incluir una sola aproximación dentro de cada objeto retornado por el feed.
        AsteroidSortOption.SPEED -> filtered.sortedByDescending {
            it.closeApproachData.firstOrNull()?.relativeVelocity?.kilometersPerHour
                ?.toDoubleOrNull()
        }
        // Los registros sin distancia conocida quedan al final en vez de parecer los más cercanos.
        AsteroidSortOption.DISTANCE -> filtered.sortedBy {
            it.closeApproachData.firstOrNull()?.missDistance?.kilometers
                ?.toDoubleOrNull() ?: Double.MAX_VALUE
        }
    }
}

/** Implementa el comportamiento asociado a calculate asteroid statistics. */
fun calculateAsteroidStatistics(asteroids: List<AsteroidDto>) = AsteroidStatistics(
    count = asteroids.size,
    hazardousCount = asteroids.count { it.isPotentiallyHazardous == true },
    largestDiameterMeters = asteroids.mapNotNull {
        it.estimatedDiameter?.meters?.maximum
    }.maxOrNull()
)
