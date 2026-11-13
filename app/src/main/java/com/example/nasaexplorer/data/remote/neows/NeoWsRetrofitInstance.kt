package com.example.nasaexplorer.data.remote.neows

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
