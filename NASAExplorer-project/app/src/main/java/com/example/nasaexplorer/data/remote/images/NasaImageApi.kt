/** Declara los endpoints HTTP utilizados por el servicio remoto correspondiente. */
package com.example.nasaexplorer.data.remote.images

import retrofit2.http.GET
import retrofit2.http.Query

/** Define el contrato que deben cumplir las implementaciones relacionadas. */
interface NasaImageApi {
    /** Realiza la operación HTTP correspondiente al servicio remoto. */
    @GET("search")
    suspend fun searchImages(
        @Query("q") query: String,
        @Query("media_type") mediaType: String = "image",
        @Query("page_size") pageSize: Int = 50
    ): NasaImageSearchResponse
}
