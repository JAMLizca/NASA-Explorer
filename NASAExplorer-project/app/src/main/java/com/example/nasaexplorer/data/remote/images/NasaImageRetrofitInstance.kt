/** Construye y configura el cliente HTTP compartido de la API. */
package com.example.nasaexplorer.data.remote.images

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Expone una instancia compartida para esta responsabilidad. */
object NasaImageRetrofitInstance {
    val api: NasaImageApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://images-api.nasa.gov/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NasaImageApi::class.java)
    }
}
