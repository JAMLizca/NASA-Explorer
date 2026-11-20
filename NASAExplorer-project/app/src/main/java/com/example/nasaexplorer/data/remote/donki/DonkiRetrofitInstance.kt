/** Construye y configura el cliente HTTP compartido de la API. */
package com.example.nasaexplorer.data.remote.donki

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Expone una instancia compartida para esta responsabilidad. */
object DonkiRetrofitInstance {
    val api: DonkiApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.nasa.gov/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DonkiApi::class.java)
    }
}
