/** Declara los endpoints HTTP utilizados por el servicio remoto correspondiente. */
package com.example.nasaexplorer.data.remote.apod

import retrofit2.http.GET

/** Define el contrato que deben cumplir las implementaciones relacionadas. */
interface ApodApi {

    /** Realiza la operación HTTP correspondiente al servicio remoto. */
    @GET("wp-json/wp/v2/apod-basic")
    suspend fun getTodayApod(): List<ApodDto>
}
