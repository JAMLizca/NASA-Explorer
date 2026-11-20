/** Define los endpoints HTTP y sus respuestas para el servicio remoto. */
package com.example.nasaexplorer.data.remote.neows

import retrofit2.http.GET
import retrofit2.http.Query

/** Define el contrato de neo ws api. */
interface NeoWsApi {

    @GET("neo/rest/v1/feed")
    suspend fun getAsteroids(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("api_key") apiKey: String
    ): NeoWsDto
}
