/** Construye y configura el cliente HTTP compartido de la API. */
package com.example.nasaexplorer.data.remote.eonet

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Expone una instancia compartida para esta responsabilidad. */
object EonetRetrofitInstance {
    val api: EonetApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://eonet.gsfc.nasa.gov/api/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EonetApi::class.java)
    }
}
