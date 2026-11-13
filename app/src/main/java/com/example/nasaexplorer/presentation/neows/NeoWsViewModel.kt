package com.example.nasaexplorer.presentation.neows

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nasaexplorer.data.remote.neows.AsteroidDto
import com.example.nasaexplorer.data.repository.NeoWsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed interface AsteroidsUiState {
    data object Loading : AsteroidsUiState
    data class Success(val asteroids: List<AsteroidDto>) : AsteroidsUiState
    data class Error(val message: String) : AsteroidsUiState
}

class NeoWsViewModel(
    private val repository: NeoWsRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<AsteroidsUiState>(AsteroidsUiState.Loading)

    val uiState: StateFlow<AsteroidsUiState> = _uiState.asStateFlow()

    private val apiKey = "DEMO_KEY"

    init {
        loadAsteroids()
    }

    fun loadAsteroids() {
        viewModelScope.launch {
            _uiState.value = AsteroidsUiState.Loading

            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            val result = repository.getAsteroids(
                startDate = today,
                endDate = today,
                apiKey = apiKey
            )

            result.fold(
                onSuccess = { response ->
                    val asteroids = response.nearEarthObjects.values.flatten()
                    _uiState.value = AsteroidsUiState.Success(asteroids)
                },
                onFailure = { error ->
                    _uiState.value = AsteroidsUiState.Error(
                        error.message ?: "No se pudieron cargar los asteroides."
                    )
                }
            )
        }
    }
}
