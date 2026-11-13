package com.example.nasaexplorer.presentation.neows

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nasaexplorer.BuildConfig
import com.example.nasaexplorer.data.remote.neows.AsteroidDto
import com.example.nasaexplorer.data.repository.NeoWsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AsteroidSortOption {
    NAME,
    DIAMETER,
    SPEED,
    DISTANCE
}

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

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _onlyHazardous = MutableStateFlow(false)
    val onlyHazardous: StateFlow<Boolean> = _onlyHazardous

    private val _sortOption = MutableStateFlow(AsteroidSortOption.NAME)
    val sortOption: StateFlow<AsteroidSortOption> = _sortOption

    val filteredAsteroids = combine(
        uiState,
        searchQuery,
        onlyHazardous,
        sortOption
    ) { state, query, hazardous, sort ->
        if (state is AsteroidsUiState.Success) {
            val filtered = state.asteroids.filter { asteroid ->
                val matchesName = asteroid.name
                    ?.contains(query, ignoreCase = true) == true

                val matchesHazard = !hazardous || asteroid.isPotentiallyHazardous

                matchesName && matchesHazard
            }

            when (sort) {
                AsteroidSortOption.NAME ->
                    filtered.sortedBy {
                        it.name.orEmpty().lowercase()
                    }

                AsteroidSortOption.DIAMETER ->
                    filtered.sortedByDescending {
                        it.estimatedDiameter?.meters?.maximum ?: 0.0
                    }

                AsteroidSortOption.SPEED ->
                    filtered.sortedByDescending {
                        it.closeApproachData
                            .firstOrNull()
                            ?.relativeVelocity
                            ?.kilometersPerHour
                            ?.toDoubleOrNull() ?: 0.0
                    }

                AsteroidSortOption.DISTANCE ->
                    filtered.sortedBy {
                        it.closeApproachData
                            .firstOrNull()
                            ?.missDistance
                            ?.kilometers
                            ?.toDoubleOrNull() ?: Double.MAX_VALUE
                    }
            }
        } else {
            emptyList()
        }
    }

    private val apiKey = BuildConfig.NASA_API_KEY

    init {
        loadAsteroids()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateHazardousFilter(enabled: Boolean) {
        _onlyHazardous.value = enabled
    }

    fun updateSortOption(option: AsteroidSortOption) {
        _sortOption.value = option
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
