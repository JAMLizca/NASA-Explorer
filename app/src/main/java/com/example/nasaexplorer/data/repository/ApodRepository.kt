package com.example.nasaexplorer.data.repository

import com.example.nasaexplorer.data.remote.apod.ApodApi
import com.example.nasaexplorer.data.remote.apod.ApodDto
import java.io.IOException
import retrofit2.HttpException

class ApodRepository(
    private val api: ApodApi
) {

    suspend fun getTodayApod(): Result<ApodDto> {
        return try {
            val list = api.getTodayApod()
            val apod = list.firstOrNull() ?: throw Exception("No se encontró información de APOD hoy.")
            Result.success(apod)

        } catch (e: IOException) {
            Result.failure(
                Exception("No se pudo conectar con NASA. Revisa tu conexión.")
            )

        } catch (e: HttpException) {
            Result.failure(
                Exception("NASA respondió con un error: ${e.code()}")
            )

        } catch (e: Exception) {
            Result.failure(
                Exception("Ocurrió un error inesperado: ${e.message}")
            )
        }
    }
}
