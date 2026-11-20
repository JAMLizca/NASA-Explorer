/** Representa una imagen satelital asociada a un evento natural. */
package com.example.nasaexplorer.data.remote.eonet

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import java.util.Locale

internal fun EonetEventDto.satelliteImageUrl(): String? {
    // La última geometría disponible alinea la fecha de la imagen con la actualización más reciente.
    val geometryWithLocation = geometry
        .sortedByDescending(EonetGeometryDto::date)
        .firstNotNullOfOrNull { geometry ->
            val coordinates = geometry.coordinates?.firstCoordinatePair()
                ?: return@firstNotNullOfOrNull null
            val date = geometry.date.take(10).takeIf { it.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) }
                ?: return@firstNotNullOfOrNull null
            Triple(coordinates.first, coordinates.second, date)
        } ?: return null

    val (longitude, latitude, date) = geometryWithLocation
    // GIBS devuelve un recorte regional, no una fotografía del fenómeno ni necesariamente del mismo instante.
    val halfSpan = 1.2
    val west = (longitude - halfSpan).coerceAtLeast(-180.0)
    val east = (longitude + halfSpan).coerceAtMost(180.0)
    val south = (latitude - halfSpan).coerceAtLeast(-90.0)
    val north = (latitude + halfSpan).coerceAtMost(90.0)

    return String.format(
        Locale.US,
        "https://gibs.earthdata.nasa.gov/wms/epsg4326/best/wms.cgi" +
            "?SERVICE=WMS&REQUEST=GetMap&VERSION=1.1.1" +
            "&LAYERS=MODIS_Terra_CorrectedReflectance_TrueColor&STYLES=" +
            "&FORMAT=image/jpeg&SRS=EPSG:4326&BBOX=%.4f,%.4f,%.4f,%.4f" +
            "&WIDTH=900&HEIGHT=460&TIME=%s",
        west,
        south,
        east,
        north,
        date
    )
}

private fun JsonElement.firstCoordinatePair(): Pair<Double, Double>? {
    if (!isJsonArray) return null
    val array = asJsonArray
    if (array.size() >= 2) {
        val longitude = array[0].numberOrNull()
        val latitude = array[1].numberOrNull()
        if (longitude != null && latitude != null &&
            longitude in -180.0..180.0 && latitude in -90.0..90.0
        ) {
            return longitude to latitude
        }
    }
    return array.firstNotNullOfOrNull(JsonElement::firstCoordinatePair)
}

private fun JsonElement.numberOrNull(): Double? {
    if (!isJsonPrimitive) return null
    val primitive: JsonPrimitive = asJsonPrimitive
    if (!primitive.isNumber) return null
    return primitive.asDouble.takeIf(Double::isFinite)
}
