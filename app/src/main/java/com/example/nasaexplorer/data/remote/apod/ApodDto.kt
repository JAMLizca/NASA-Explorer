package com.example.nasaexplorer.data.remote.apod

import com.google.gson.annotations.SerializedName

data class ApodDto(

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
    val alt: String? = null
)
