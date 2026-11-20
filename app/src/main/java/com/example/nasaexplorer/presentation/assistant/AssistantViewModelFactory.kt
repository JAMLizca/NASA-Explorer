/** Crea el ViewModel correspondiente proporcionando sus dependencias. */
package com.example.nasaexplorer.presentation.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nasaexplorer.data.repository.AssistantHistoryRepository
import com.example.nasaexplorer.data.repository.AiRepository

/** Construye una instancia del ViewModel con sus dependencias necesarias. */
class AssistantViewModelFactory(
    private val repository: AiRepository = AiRepository(),
    private val historyRepository: AssistantHistoryRepository? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AssistantViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AssistantViewModel(repository, historyRepository) as T
        }
        throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}
