package com.example.nasaexplorer.data.remote.apod

import retrofit2.http.GET

interface ApodApi {

    @GET("wp-json/wp/v2/apod-basic")
    suspend fun getTodayApod(): List<ApodDto>
}
