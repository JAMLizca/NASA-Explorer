/** Construye y configura el cliente HTTP compartido de la API. */
package com.example.nasaexplorer.data.remote.neows

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Expone una instancia compartida para esta responsabilidad. */
object NeoWsRetrofitInstance {

    private const val BASE_URL = "https://api.nasa.gov/"

    val neoWsApi: NeoWsApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NeoWsApi::class.java)
    }
}
