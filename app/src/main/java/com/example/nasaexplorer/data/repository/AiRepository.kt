/** Orquesta las fuentes de datos y ofrece una interfaz coherente para la presentación. */
package com.example.nasaexplorer.data.repository

import android.util.Log
import com.example.nasaexplorer.data.remote.ai.AiApi
import com.example.nasaexplorer.data.remote.ai.AiRequestDto
import com.example.nasaexplorer.BuildConfig
import com.google.gson.Gson
import com.google.gson.JsonParseException
import kotlinx.coroutines.CancellationException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private data class BackendErrorDto(val detail: String? = null)

/** Centraliza el acceso y manejo de datos para ai. */
class AiRepository {

    private val api: AiApi by lazy {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .callTimeout(70, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BuildConfig.AI_BACKEND_URL.trimEnd('/') + "/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AiApi::class.java)
    }

    suspend fun askAssistant(
        question: String,
        context: Map<String, String>
    ): Result<String> = try {
        require(question.isNotBlank()) { "Escribe una pregunta para el asistente." }
        require(context.isNotEmpty()) { "No hay contexto de NASA para explicar." }
        val response = api.askAssistant(
            AiRequestDto(question = question.trim(), context = context)
        )
        Result.success("${response.explanation}\n\n${response.sourceNote}")
    } catch (error: CancellationException) {
        throw error
    } catch (error: IOException) {
        Log.e("AiRepository", "Network request to assistant backend failed", error)
        Result.failure(IOException(networkErrorMessage(error), error))
    } catch (error: HttpException) {
        Result.failure(IllegalStateException(errorMessage(error), error))
    } catch (error: Exception) {
        Result.failure(error)
    }

    private fun errorMessage(error: HttpException): String {
        val detail = try {
            error.response()?.errorBody()?.string()
                ?.let { Gson().fromJson(it, BackendErrorDto::class.java)?.detail }
        } catch (_: IOException) {
            null
        } catch (_: JsonParseException) {
            null
        }
        return detail?.takeIf(String::isNotBlank)
            ?: "El asistente respondió con error HTTP ${error.code()}."
    }

    private fun networkErrorMessage(error: IOException): String {
        var rootCause: Throwable = error
        while (rootCause.cause != null && rootCause.cause !== rootCause) {
            rootCause = rootCause.cause!!
        }
        return when (rootCause) {
            is UnknownHostException ->
                "No se pudo resolver el servidor del asistente. Revisa la conexión a Internet o el DNS del teléfono."
            is SocketTimeoutException ->
                "La conexión con el backend tardó demasiado. Espera un momento y vuelve a intentarlo."
            else ->
                "No se pudo conectar con el backend del asistente (${rootCause.javaClass.simpleName}: ${rootCause.message ?: "sin detalle"})."
        }
    }
}
