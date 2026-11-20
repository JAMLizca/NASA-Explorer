/** Modela los datos JSON recibidos desde el servicio remoto. */
package com.example.nasaexplorer.data.remote.apod

import com.google.gson.annotations.SerializedName

/** Representa la estructura de datos intercambiada con una API. */
data class ApodDto(

    @SerializedName("post_id")
    val postId: Long? = null,

    @SerializedName("date")
    val date: String? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("explanation")
    val explanation: String? = null,

    @SerializedName("media_type")
    val mediaType: String? = null,

    @SerializedName("url")
    val url: String? = null,

    @SerializedName("hdurl")
    val hdUrl: String? = null,

    @SerializedName("credit")
    val credit: String? = null,

    @SerializedName("copyright")
    val copyright: String? = null,

    @SerializedName("alt")
    val alt: String? = null,

    @SerializedName("permalink")
    val permalink: String? = null,

    @SerializedName("basic_html")
    val basicHtml: String? = null,

    @SerializedName("basic_html_url")
    val basicHtmlUrl: String? = null
)
