/** Convierte las geometrías EONET en puntos aproximados para representar en el mapa. */
package com.example.nasaexplorer.presentation.eonet

import com.example.nasaexplorer.data.remote.eonet.EonetEventDto
import com.google.gson.JsonElement

/** Datos mínimos de un evento activo que puede ubicarse en el mapa. */
data class EonetMapPoint(
    val id: String,
    val title: String,
    val description: String?,
    val categoryId: String,
    val category: String,
    val categoryDescription: String?,
    val date: String,
    val latitude: Double,
    val longitude: Double,
    val magnitudeValue: Double?,
    val magnitudeUnit: String?
)

/** Usa el centro aproximado de la geometría disponible más reciente de cada evento. */
internal fun List<EonetEventDto>.toMapPoints(): List<EonetMapPoint> = mapNotNull { event ->
    val geometryAndCoordinates = event.geometry
        .sortedByDescending { it.date }
        .firstNotNullOfOrNull { geometry ->
            val coordinates = geometry.coordinates?.coordinatePairs().orEmpty()
            if (coordinates.isNotEmpty()) geometry to coordinates else null
        } ?: return@mapNotNull null

    val (geometry, coordinates) = geometryAndCoordinates
    // EONET cierra los anillos de los polígonos repitiendo el primer punto; se excluye para no sesgar el centro.
    val boundary = if (coordinates.size > 1 && coordinates.first() == coordinates.last()) {
        coordinates.dropLast(1)
    } else {
        coordinates
    }
    // Es un promedio de vértices para ubicar el marcador, no un centroide geográfico exacto.
    val latitude = boundary.map(GeoCoordinate::latitude).average()
    val longitude = boundary.map(GeoCoordinate::longitude).average()
    EonetMapPoint(
        id = event.id,
        title = event.title,
        description = event.description?.trim()?.takeIf(String::isNotEmpty),
        categoryId = event.categories.firstOrNull()?.id.orEmpty(),
        category = event.categories.firstOrNull()?.title.orEmpty(),
        categoryDescription = eonetCategoryDescription(event.categories.firstOrNull()?.id),
        date = geometry.date,
        latitude = latitude,
        longitude = longitude,
        magnitudeValue = geometry.magnitudeValue,
        magnitudeUnit = geometry.magnitudeUnit
    )
}

/** Returns the category definition published by NASA EONET, translated for the Spanish UI. */
private fun eonetCategoryDescription(categoryId: String?): String? = when (categoryId) {
    "drought" -> "Ausencia prolongada de precipitaciones que afecta la agricultura, la ganadería y la disponibilidad de alimentos y agua."
    "dustHaze" -> "Eventos relacionados con tormentas de polvo, contaminación del aire y otros aerosoles no volcánicos."
    "earthquakes" -> "Sacudidas y desplazamientos del terreno; algunas consecuencias también pueden aparecer como inundaciones o deslizamientos."
    "floods" -> "Inundaciones y extensión del agua más allá de los límites habituales de ríos y lagos."
    "landslides" -> "Deslizamientos de tierra y fenómenos relacionados, como flujos de lodo y avalanchas."
    "manmade" -> "Eventos extremos originados por actividades humanas."
    "seaLakeIce" -> "Hielo en océanos y lagos, incluido el hielo estacional o permanente y los icebergs."
    "severeStorms" -> "Aspectos atmosféricos de tormentas, como huracanes, ciclones y tornados; sus efectos también pueden aparecer en otras categorías."
    "snow" -> "Nevadas extremas o anómalas por su momento, extensión o profundidad."
    "tempExtremes" -> "Temperaturas terrestres anómalas, tanto calor como frío."
    "volcanoes" -> "Efectos físicos de erupciones, como roca, ceniza y lava, y columnas atmosféricas de ceniza o gases."
    "waterColor" -> "Eventos que alteran el aspecto del agua, como fitoplancton, mareas rojas, algas y sedimentos."
    "wildfires" -> "Incendios en zonas naturales, bosques o llanuras; pueden tener origen natural o humano."
    else -> null
}

private data class GeoCoordinate(val longitude: Double, val latitude: Double)

/** Finds coordinate pairs recursively because EONET geometries can be points or polygons. */
private fun JsonElement.coordinatePairs(): List<GeoCoordinate> {
    if (!isJsonArray) return emptyList()

    val values = asJsonArray
    if (values.size() >= 2 &&
        values[0].isJsonPrimitive && values[0].asJsonPrimitive.isNumber &&
        values[1].isJsonPrimitive && values[1].asJsonPrimitive.isNumber
    ) {
        val longitude = values[0].asDouble
        val latitude = values[1].asDouble
        return if (longitude in -180.0..180.0 && latitude in -90.0..90.0) {
            listOf(GeoCoordinate(longitude, latitude))
        } else {
            emptyList()
        }
    }

    return values.flatMap(JsonElement::coordinatePairs)
}
