/** Define los endpoints HTTP y sus respuestas para el servicio remoto. */
package com.example.nasaexplorer.data.remote.images

import retrofit2.http.GET
import retrofit2.http.Query

/** Define el contrato de nasa image api. */
interface NasaImageApi {
    @GET("search")
    suspend fun searchImages(
        @Query("q") query: String,
        @Query("media_type") mediaType: String = "image",
        @Query("page_size") pageSize: Int = 50
    ): NasaImageSearchResponse
}
