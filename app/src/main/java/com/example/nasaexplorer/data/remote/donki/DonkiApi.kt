/** Define los endpoints HTTP y sus respuestas para el servicio remoto. */
package com.example.nasaexplorer.data.remote.donki

import retrofit2.http.GET
import retrofit2.http.Query

/** Define el contrato de donki api. */
interface DonkiApi {
    @GET("DONKI/notifications")
    suspend fun getNotifications(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("type") type: String = "all",
        @Query("api_key") apiKey: String
    ): List<DonkiNotificationDto>
}
