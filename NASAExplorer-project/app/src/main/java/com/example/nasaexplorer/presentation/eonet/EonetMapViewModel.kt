/** Carga los eventos EONET necesarios para la pantalla de mapa. */
package com.example.nasaexplorer.presentation.eonet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nasaexplorer.data.repository.EonetRepository
import com.example.nasaexplorer.presentation.home.FeedUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Mantiene el estado de carga y permite volver a solicitar los eventos activos. */
class EonetMapViewModel(
    private val repository: EonetRepository
) : ViewModel() {
    private val _state = MutableStateFlow<FeedUiState<EonetMapPoint>>(FeedUiState.Loading)
    val state: StateFlow<FeedUiState<EonetMapPoint>> = _state.asStateFlow()

    init {
        loadEvents()
    }

    fun loadEvents() {
        viewModelScope.launch {
            _state.value = FeedUiState.Loading
            repository.getOpenEvents(limit = MAP_EVENT_LIMIT).fold(
                onSuccess = { events ->
                    _state.value = FeedUiState.Success(events.toMapPoints())
                },
                onFailure = { error ->
                    if (error is CancellationException) throw error
                    _state.value = FeedUiState.Error(
                        error.message ?: "No se pudieron cargar los eventos EONET."
                    )
                }
            )
        }
    }

    private companion object {
        const val MAP_EVENT_LIMIT = 100
    }
}
