/** Administra el estado y coordina las operaciones de su pantalla. */
package com.example.nasaexplorer.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nasaexplorer.data.remote.images.NasaAsteroidPhoto
import com.example.nasaexplorer.data.repository.NasaAsteroidImageRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Define el contrato que deben cumplir las implementaciones relacionadas. */
sealed interface AsteroidImageUiState {
    /** Expone una instancia compartida para esta responsabilidad. */
    data object Loading : AsteroidImageUiState
    /** Encapsula la responsabilidad principal de esta sección. */
    data class Available(val photo: NasaAsteroidPhoto) : AsteroidImageUiState
    /** Expone una instancia compartida para esta responsabilidad. */
    data object Unavailable : AsteroidImageUiState
    /** Expone una instancia compartida para esta responsabilidad. */
    data object Error : AsteroidImageUiState
}

/** Mantiene el estado de la pantalla y coordina sus operaciones. */
class AsteroidImageViewModel(
    private val repository: NasaAsteroidImageRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<AsteroidImageUiState>(AsteroidImageUiState.Loading)
    val uiState: StateFlow<AsteroidImageUiState> = _uiState.asStateFlow()
    private var searchJob: Job? = null
    private var currentName: String? = null

    /** Actualiza o expone el estado requerido por la pantalla. */
    fun load(asteroidName: String, forceRefresh: Boolean = false) {
        if (!forceRefresh && currentName == asteroidName && searchJob?.isActive == true) return
        if (!forceRefresh && currentName == asteroidName &&
            _uiState.value !is AsteroidImageUiState.Error
        ) return

        currentName = asteroidName
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _uiState.value = AsteroidImageUiState.Loading
            val result = repository.findVerifiedPhoto(asteroidName)
            _uiState.value = result.fold(
                onSuccess = { photo ->
                    if (photo == null) AsteroidImageUiState.Unavailable
                    else AsteroidImageUiState.Available(photo)
                },
                onFailure = { error ->
                    if (error is CancellationException) throw error
                    AsteroidImageUiState.Error
                }
            )
        }
    }
}
