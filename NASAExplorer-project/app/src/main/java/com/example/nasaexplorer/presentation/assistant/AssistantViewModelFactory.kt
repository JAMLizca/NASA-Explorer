/** Construye el ViewModel correspondiente con sus dependencias. */
package com.example.nasaexplorer.presentation.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nasaexplorer.data.repository.AssistantHistoryRepository
import com.example.nasaexplorer.data.repository.AiRepository

/** Crea el ViewModel proporcionando las dependencias necesarias. */
class AssistantViewModelFactory(
    private val repository: AiRepository = AiRepository(),
    private val historyRepository: AssistantHistoryRepository? = null
) : ViewModelProvider.Factory {
    /** Actualiza o expone el estado requerido por la pantalla. */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AssistantViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AssistantViewModel(repository, historyRepository) as T
        }
        throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}
