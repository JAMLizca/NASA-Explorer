/** Define el contrato y los datos para comunicarse con el asistente de IA. */
package com.example.nasaexplorer.data.remote.ai

import com.google.gson.annotations.SerializedName

/** Representa la estructura de datos recibida desde una API. */
data class AiRequestDto(
    @SerializedName("question")
    val question: String,
    @SerializedName("context")
    val context: Map<String, String>
)

/** Representa la estructura de datos recibida desde una API. */
data class AiResponseDto(
    @SerializedName("explanation")
    val explanation: String,
    @SerializedName("sourceNote")
    val sourceNote: String
)
