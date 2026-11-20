/** Declara los endpoints HTTP utilizados por el servicio remoto correspondiente. */
package com.example.nasaexplorer.data.remote.donki

import retrofit2.http.GET
import retrofit2.http.Query

/** Define el contrato que deben cumplir las implementaciones relacionadas. */
interface DonkiApi {
    /** Realiza la operación HTTP correspondiente al servicio remoto. */
    @GET("DONKI/notifications")
    suspend fun getNotifications(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("type") type: String = "all",
        @Query("api_key") apiKey: String
    ): List<DonkiNotificationDto>
}
