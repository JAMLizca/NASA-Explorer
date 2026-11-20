/** Modela las respuestas JSON recibidas desde el servicio remoto. */
package com.example.nasaexplorer.data.remote.eonet

import com.google.gson.annotations.SerializedName
import com.google.gson.JsonElement

/** Representa la estructura de datos recibida desde una API. */
data class EonetEventsDto(
    val events: List<EonetEventDto> = emptyList()
)

/** Representa la estructura de datos recibida desde una API. */
data class EonetEventDto(
    val id: String = "",
    val title: String = "",
    val description: String? = null,
    val link: String = "",
    val categories: List<EonetCategoryDto> = emptyList(),
    val geometry: List<EonetGeometryDto> = emptyList()
)

/** Representa la estructura de datos recibida desde una API. */
data class EonetCategoryDto(
    val id: String = "",
    val title: String = ""
)

/** Representa la estructura de datos recibida desde una API. */
data class EonetGeometryDto(
    val date: String = "",
    @SerializedName("magnitudeValue")
    val magnitudeValue: Double? = null,
    @SerializedName("magnitudeUnit")
    val magnitudeUnit: String? = null,
    val coordinates: JsonElement? = null
)
