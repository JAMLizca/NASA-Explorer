/** Modela los datos JSON recibidos desde el servicio remoto. */
package com.example.nasaexplorer.data.remote.images

import com.google.gson.annotations.SerializedName

/** Encapsula la responsabilidad principal de esta sección. */
data class NasaImageSearchResponse(
    val collection: NasaImageCollection? = null
)

/** Encapsula la responsabilidad principal de esta sección. */
data class NasaImageCollection(
    val items: List<NasaImageItem> = emptyList()
)

/** Encapsula la responsabilidad principal de esta sección. */
data class NasaImageItem(
    val data: List<NasaImageMetadata> = emptyList(),
    val links: List<NasaImageLink> = emptyList()
)

/** Encapsula la responsabilidad principal de esta sección. */
data class NasaImageMetadata(
    @SerializedName("nasa_id")
    val nasaId: String? = null,
    val title: String? = null,
    val description: String? = null,
    @SerializedName("date_created")
    val dateCreated: String? = null
)

/** Encapsula la responsabilidad principal de esta sección. */
data class NasaImageLink(
    val href: String? = null,
    val render: String? = null,
    val rel: String? = null
)

/** Encapsula la responsabilidad principal de esta sección. */
data class NasaAsteroidPhoto(
    val imageUrl: String,
    val title: String,
    val nasaId: String
)
