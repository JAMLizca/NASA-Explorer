package com.example.nasaexplorer.data.remote.neows

import com.google.gson.annotations.SerializedName

data class NeoWsDto(
    @SerializedName("element_count")
    val elementCount: Int = 0,

    @SerializedName("near_earth_objects")
    val nearEarthObjects: Map<String, List<AsteroidDto>> = emptyMap()
)

data class AsteroidDto(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("estimated_diameter")
    val estimatedDiameter: EstimatedDiameterDto? = null,

    @SerializedName("is_potentially_hazardous_asteroid")
    val isPotentiallyHazardous: Boolean = false,

    @SerializedName("close_approach_data")
    val closeApproachData: List<CloseApproachDto> = emptyList()
)

data class EstimatedDiameterDto(
    @SerializedName("meters")
    val meters: DiameterRangeDto? = null
)

data class DiameterRangeDto(
    @SerializedName("estimated_diameter_min")
    val minimum: Double = 0.0,

    @SerializedName("estimated_diameter_max")
    val maximum: Double = 0.0
)

data class CloseApproachDto(
    @SerializedName("close_approach_date")
    val date: String? = null,

    @SerializedName("relative_velocity")
    val relativeVelocity: RelativeVelocityDto? = null,

    @SerializedName("miss_distance")
    val missDistance: MissDistanceDto? = null
)

data class RelativeVelocityDto(
    @SerializedName("kilometers_per_hour")
    val kilometersPerHour: String? = null
)

data class MissDistanceDto(
    @SerializedName("kilometers")
    val kilometers: String? = null
)
