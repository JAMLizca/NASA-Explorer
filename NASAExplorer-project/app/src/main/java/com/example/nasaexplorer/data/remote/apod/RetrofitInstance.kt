/** Construye y configura el cliente HTTP compartido de la API. */
package com.example.nasaexplorer.data.remote.apod

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Expone una instancia compartida para esta responsabilidad. */
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
