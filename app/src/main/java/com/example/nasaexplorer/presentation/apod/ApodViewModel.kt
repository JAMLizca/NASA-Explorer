package com.example.nasaexplorer.presentation.apod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nasaexplorer.data.remote.apod.ApodDto
import com.example.nasaexplorer.data.repository.ApodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ApodUiState {
    data object Loading : ApodUiState
    data class Success(val apod: ApodDto) : ApodUiState
    data class Error(val message: String) : ApodUiState
}

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
