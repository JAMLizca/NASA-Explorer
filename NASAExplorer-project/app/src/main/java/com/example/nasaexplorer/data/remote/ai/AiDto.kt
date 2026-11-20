/** Define el contrato y los modelos para la comunicación con el asistente de IA. */
package com.example.nasaexplorer.data.remote.ai

import com.google.gson.annotations.SerializedName

/** Representa la estructura de datos intercambiada con una API. */
data class AiRequestDto(
    @SerializedName("question")
    val question: String,
    @SerializedName("context")
    val context: Map<String, String>
)

/** Representa la estructura de datos intercambiada con una API. */
data class AiResponseDto(
    @SerializedName("explanation")
    val explanation: String,
    @SerializedName("sourceNote")
    val sourceNote: String
)
