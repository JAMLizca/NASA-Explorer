/** Define los endpoints HTTP y sus respuestas para el servicio remoto. */
package com.example.nasaexplorer.data.remote.apod

import retrofit2.http.GET

/** Define el contrato de apod api. */
interface ApodApi {

    @GET("wp-json/wp/v2/apod-basic")
    suspend fun getTodayApod(): List<ApodDto>
}
