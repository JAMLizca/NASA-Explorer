/** Declara los endpoints HTTP utilizados por el servicio remoto correspondiente. */
package com.example.nasaexplorer.data.remote.neows

import retrofit2.http.GET
import retrofit2.http.Query

/** Define el contrato que deben cumplir las implementaciones relacionadas. */
interface NeoWsApi {

    /** Realiza la operación HTTP correspondiente al servicio remoto. */
    @GET("neo/rest/v1/feed")
    suspend fun getAsteroids(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("api_key") apiKey: String
    ): NeoWsDto
}
