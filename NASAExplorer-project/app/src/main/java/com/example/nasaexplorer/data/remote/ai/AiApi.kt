/** Define el contrato y los modelos para la comunicación con el asistente de IA. */
package com.example.nasaexplorer.data.remote.ai

import retrofit2.http.Body
import retrofit2.http.POST

/** Define el contrato que deben cumplir las implementaciones relacionadas. */
interface AiApi {
    /** Realiza la operación HTTP correspondiente al servicio remoto. */
    @POST("api/assistant/explain")
    suspend fun askAssistant(
        @Body request: AiRequestDto
    ): AiResponseDto
}
