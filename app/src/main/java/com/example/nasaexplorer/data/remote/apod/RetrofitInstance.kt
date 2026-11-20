/** Construye y configura el cliente Retrofit usado por esta API. */
package com.example.nasaexplorer.data.remote.apod

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Expone una instancia compartida para retrofit instance. */
object RetrofitInstance {

    private const val BASE_URL = "https://science.nasa.gov/"

    val apodApi: ApodApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApodApi::class.java)
    }
}
