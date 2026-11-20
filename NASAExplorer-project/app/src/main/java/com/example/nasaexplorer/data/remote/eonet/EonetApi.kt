/** Define los endpoints HTTP y sus respuestas para el servicio remoto. */
package com.example.nasaexplorer.data.remote.eonet

import retrofit2.http.GET
import retrofit2.http.Query

/** Define el contrato de eonet api. */
interface EonetApi {
    @GET("events")
    suspend fun getOpenEvents(
        @Query("status") status: String = "open",
        @Query("limit") limit: Int = 5
    ): EonetEventsDto
}
