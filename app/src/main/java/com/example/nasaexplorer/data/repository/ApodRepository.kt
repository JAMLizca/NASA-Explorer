/** Orquesta las fuentes de datos y ofrece una interfaz coherente para la presentación. */
package com.example.nasaexplorer.data.repository

import com.example.nasaexplorer.data.remote.apod.ApodApi
import com.example.nasaexplorer.data.remote.apod.ApodDto
import java.io.IOException
import retrofit2.HttpException

/** Centraliza el acceso y manejo de datos para apod. */
class ApodRepository(
    private val api: ApodApi
) {

    suspend fun getTodayApod(): Result<ApodDto> {
        return try {
            val list = api.getTodayApod()
            val apod = list.firstOrNull()
                ?: throw IllegalStateException("NASA no devolvió información de APOD.")
            require(!apod.title.isNullOrBlank() && !apod.date.isNullOrBlank()) {
                "La respuesta de NASA no contiene los datos básicos de APOD."
            }
            Result.success(apod)

        } catch (e: IOException) {
            Result.failure(IOException("No se pudo conectar con NASA. Revisa tu conexión.", e))

        } catch (e: HttpException) {
            Result.failure(IllegalStateException("NASA respondió con un error: ${e.code()}", e))

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
