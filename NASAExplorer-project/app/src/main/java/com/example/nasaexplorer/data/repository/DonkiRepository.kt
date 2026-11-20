/** Orquesta las fuentes de datos y presenta resultados coherentes a la capa de interfaz. */
package com.example.nasaexplorer.data.repository

import com.google.gson.JsonParseException
import com.example.nasaexplorer.data.remote.donki.DonkiApi
import com.example.nasaexplorer.data.remote.donki.DonkiNotificationDto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/** Centraliza el acceso a datos y la resolución de fuentes. */
class DonkiRepository(
    private val api: DonkiApi
) {
    /** Proporciona la operación de datos requerida por la interfaz. */
    suspend fun getRecentNotifications(apiKey: String): Result<List<DonkiNotificationDto>> =
        withContext(Dispatchers.IO) {
            try {
                require(apiKey.isNotBlank()) { "La clave NASA_API_KEY no está configurada." }
                val utc = TimeZone.getTimeZone("UTC")
                val today = Calendar.getInstance(utc)
                val endDate = formatDate(today.timeInMillis, utc)
                today.add(Calendar.DAY_OF_YEAR, -6)
                val startDate = formatDate(today.timeInMillis, utc)
                val notifications = api.getNotifications(
                    startDate = startDate,
                    endDate = endDate,
                    apiKey = apiKey
                )
                Result.success(
                    notifications.sortedByDescending(DonkiNotificationDto::messageIssueTime)
                        .take(MAX_NOTIFICATIONS)
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: IllegalArgumentException) {
                Result.failure(e)
            } catch (e: JsonParseException) {
                Result.failure(IOException("DONKI devolvió una respuesta inválida.", e))
            } catch (e: IOException) {
                Result.failure(IOException("No hay conexión con DONKI.", e))
            } catch (e: HttpException) {
                val message = when (e.code()) {
                    429 -> "NASA limitó temporalmente las solicitudes de DONKI. Configura una clave NASA propia si usas DEMO_KEY."
                    403 -> "NASA rechazó la clave. Revisa NASA_API_KEY en local.properties."
                    else -> "DONKI respondió con HTTP ${e.code()}."
                }
                Result.failure(IOException(message, e))
            }
        }

    private fun formatDate(timeMillis: Long, timeZone: TimeZone): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            this.timeZone = timeZone
        }.format(timeMillis)

    private companion object {
        const val MAX_NOTIFICATIONS = 3
    }
}
