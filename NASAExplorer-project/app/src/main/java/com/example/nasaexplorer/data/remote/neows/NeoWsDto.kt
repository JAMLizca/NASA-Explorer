/** Modela los datos JSON recibidos desde el servicio remoto. */
package com.example.nasaexplorer.data.remote.neows

import com.google.gson.annotations.SerializedName

/** Representa la estructura de datos intercambiada con una API. */
data class NeoWsDto(
    @SerializedName("element_count")
    val elementCount: Int = 0,

    @SerializedName("near_earth_objects")
    val nearEarthObjects: Map<String, List<AsteroidDto>> = emptyMap()
)

/** Representa la estructura de datos intercambiada con una API. */
data class AsteroidDto(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("estimated_diameter")
    val estimatedDiameter: EstimatedDiameterDto? = null,

    @SerializedName("is_potentially_hazardous_asteroid")
    val isPotentiallyHazardous: Boolean? = null,

    @SerializedName("close_approach_data")
    val closeApproachData: List<CloseApproachDto> = emptyList()
)

/** Representa la estructura de datos intercambiada con una API. */
data class EstimatedDiameterDto(
    @SerializedName("meters")
    val meters: DiameterRangeDto? = null
)

/** Representa la estructura de datos intercambiada con una API. */
data class DiameterRangeDto(
    @SerializedName("estimated_diameter_min")
    val minimum: Double? = null,

    @SerializedName("estimated_diameter_max")
    val maximum: Double? = null
)

/** Representa la estructura de datos intercambiada con una API. */
data class CloseApproachDto(
    @SerializedName("close_approach_date")
    val date: String? = null,

    @SerializedName("relative_velocity")
    val relativeVelocity: RelativeVelocityDto? = null,

    @SerializedName("miss_distance")
    val missDistance: MissDistanceDto? = null
)

/** Representa la estructura de datos intercambiada con una API. */
data class RelativeVelocityDto(
    @SerializedName("kilometers_per_hour")
    val kilometersPerHour: String? = null
)

/** Representa la estructura de datos intercambiada con una API. */
data class MissDistanceDto(
    @SerializedName("kilometers")
    val kilometers: String? = null
)
