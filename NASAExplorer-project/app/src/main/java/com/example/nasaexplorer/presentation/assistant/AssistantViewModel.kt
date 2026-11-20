/** Gestiona el estado y coordina las operaciones de su pantalla. */
package com.example.nasaexplorer.presentation.assistant

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nasaexplorer.data.local.entity.AssistantHistoryEntity
import com.example.nasaexplorer.data.repository.AssistantHistoryRepository
import com.example.nasaexplorer.data.repository.AiRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Define el contrato de assistant ui state. */
sealed interface AssistantUiState {
    data object Idle : AssistantUiState
    data class Loading(val question: String) : AssistantUiState
    /** Encapsula la responsabilidad principal de success. */
    data class Success(val question: String, val answer: String) : AssistantUiState
    data class Error(val question: String, val message: String) : AssistantUiState
}

/** Mantiene y transforma el estado requerido por la pantalla de assistant. */
class AssistantViewModel(
    private val repository: AiRepository = AiRepository(),
    private val historyRepository: AssistantHistoryRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow<AssistantUiState>(AssistantUiState.Idle)
    val uiState: StateFlow<AssistantUiState> = _uiState.asStateFlow()
    val history: StateFlow<List<AssistantHistoryEntity>> =
        (historyRepository?.history ?: emptyFlow()).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )
    private val _historyError = MutableStateFlow<String?>(null)
    val historyError: StateFlow<String?> = _historyError.asStateFlow()

    /** Ejecuta o expone la operación de estado asociada a ask question. */
    fun askQuestion(prompt: String, context: Map<String, String> = emptyMap()) {
        if (prompt.isBlank()) return
        viewModelScope.launch {
            val submittedQuestion = prompt.trim()
            _historyError.value = null
            _uiState.value = AssistantUiState.Loading(submittedQuestion)
            // Permite consultas educativas generales sin fingir que hay un objeto NASA seleccionado.
            val requestContext = context.ifEmpty {
                mapOf(
                    "scope" to "General astronomy question; no specific NASA object is selected.",
                    "guidance" to "Explain general concepts only and state when current object data is unavailable."
                )
            }
            val result = repository.askAssistant(submittedQuestion, requestContext)
            _uiState.value = result.fold(
                onSuccess = { answer ->
                    try {
                        // Un fallo local del historial no debe descartar una respuesta ya recibida.
                        historyRepository?.save(submittedQuestion, answer)
                    } catch (error: CancellationException) {
                        throw error
                    } catch (error: Exception) {
                        Log.e("AssistantViewModel", "Failed to persist assistant history", error)
                        _historyError.value = "La respuesta llegó, pero no se pudo guardar en el historial."
                    }
                    AssistantUiState.Success(submittedQuestion, answer)
                },
                onFailure = {
                    if (it is CancellationException) throw it
                    AssistantUiState.Error(
                        submittedQuestion,
                        it.message ?: "Error al consultar el asistente"
                    )
                }
            )
        }
    }

    /** Ejecuta o expone la operación de estado asociada a ask. */
    fun ask(prompt: String, contextMap: Map<String, String>) {
        askQuestion(prompt, contextMap)
    }

    /** Elimina una conversación y expone el error si falla el almacenamiento. */
    fun deleteHistoryEntry(id: Long) {
        viewModelScope.launch {
            try {
                historyRepository?.delete(id)
                _historyError.value = null
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.e("AssistantViewModel", "Failed to delete assistant history entry", error)
                _historyError.value = "No se pudo eliminar esta pregunta del historial."
            }
        }
    }

    /** Elimina todo el historial y expone el error si falla el almacenamiento. */
    fun deleteAllHistory() {
        viewModelScope.launch {
            try {
                historyRepository?.deleteAll()
                _historyError.value = null
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.e("AssistantViewModel", "Failed to clear assistant history", error)
                _historyError.value = "No se pudo borrar el historial."
            }
        }
    }
}
