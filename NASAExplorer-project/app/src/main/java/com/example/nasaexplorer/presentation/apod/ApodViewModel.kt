/** Administra el estado y coordina las operaciones de su pantalla. */
package com.example.nasaexplorer.presentation.apod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nasaexplorer.data.remote.apod.ApodDto
import com.example.nasaexplorer.data.repository.ApodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Define el contrato que deben cumplir las implementaciones relacionadas. */
sealed interface ApodUiState {
    /** Expone una instancia compartida para esta responsabilidad. */
    data object Loading : ApodUiState
    /** Encapsula la responsabilidad principal de esta sección. */
    data class Success(val apod: ApodDto) : ApodUiState
    /** Encapsula la responsabilidad principal de esta sección. */
    data class Error(val message: String) : ApodUiState
}

/** Mantiene el estado de la pantalla y coordina sus operaciones. */
class ApodViewModel(
    private val repository: ApodRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<ApodUiState>(ApodUiState.Loading)

    val uiState: StateFlow<ApodUiState> =
        _uiState.asStateFlow()

    init {
        loadApod()
    }

    /** Actualiza o expone el estado requerido por la pantalla. */
    fun loadApod() {
        viewModelScope.launch {
            _uiState.value = ApodUiState.Loading

            val result = repository.getTodayApod()

            _uiState.value = result.fold(
                onSuccess = { apod ->
                    ApodUiState.Success(apod)
                },
                onFailure = { error ->
                    ApodUiState.Error(
                        error.message ?: "No se pudo cargar APOD"
                    )
                }
            )
        }
    }
}
