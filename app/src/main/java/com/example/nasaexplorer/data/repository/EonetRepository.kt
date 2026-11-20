/** Orquesta las fuentes de datos y ofrece una interfaz coherente para la presentación. */
package com.example.nasaexplorer.data.repository

import com.google.gson.JsonParseException
import com.example.nasaexplorer.data.remote.eonet.EonetApi
import com.example.nasaexplorer.data.remote.eonet.EonetEventDto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

/** Centraliza el acceso y manejo de datos para eonet. */
class EonetRepository(
    private val api: EonetApi
) {
    suspend fun getOpenEvents(limit: Int = 5): Result<List<EonetEventDto>> = withContext(Dispatchers.IO) {
        try {
            Result.success(api.getOpenEvents(limit = limit).events)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonParseException) {
            Result.failure(IOException("EONET devolvió una respuesta inválida.", e))
        } catch (e: IOException) {
            Result.failure(IOException("No hay conexión con EONET.", e))
        } catch (e: HttpException) {
            Result.failure(IOException("EONET respondió con HTTP ${e.code()}.", e))
        }
    }
}
