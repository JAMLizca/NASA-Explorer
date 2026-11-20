/** Define el contrato y los datos para comunicarse con el asistente de IA. */
package com.example.nasaexplorer.data.remote.ai

import retrofit2.http.Body
import retrofit2.http.POST

/** Define el contrato de ai api. */
interface AiApi {
    @POST("api/assistant/explain")
    suspend fun askAssistant(
        @Body request: AiRequestDto
    ): AiResponseDto
}
