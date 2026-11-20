/** Administra el estado y coordina las operaciones de su pantalla. */
package com.example.nasaexplorer.presentation.neows

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nasaexplorer.BuildConfig
import com.example.nasaexplorer.data.remote.neows.AsteroidDto
import com.example.nasaexplorer.data.repository.NeoWsApiException
import com.example.nasaexplorer.data.repository.NeoWsRepository
import com.example.nasaexplorer.domain.model.AsteroidSortOption
import com.example.nasaexplorer.domain.model.calculateAsteroidStatistics
import com.example.nasaexplorer.domain.model.filterAndSortAsteroids
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Define el contrato que deben cumplir las implementaciones relacionadas. */
sealed interface AsteroidsUiState {
    /** Expone una instancia compartida para esta responsabilidad. */
    data object Loading : AsteroidsUiState
    /** Encapsula la responsabilidad principal de esta sección. */
    data class Success(val asteroids: List<AsteroidDto>) : AsteroidsUiState
    /** Encapsula la responsabilidad principal de esta sección. */
    data class Error(
        val message: String,
        val title: String = "No pudimos cargar los asteroides",
        val httpStatusCode: Int? = null
    ) : AsteroidsUiState
}

/** Mantiene el estado de la pantalla y coordina sus operaciones. */
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

    private val _startDate = MutableStateFlow(today())
    val startDate: StateFlow<String> = _startDate.asStateFlow()

    private val _endDate = MutableStateFlow(today())
    val endDate: StateFlow<String> = _endDate.asStateFlow()

    private val _statistics = MutableStateFlow(calculateAsteroidStatistics(emptyList()))
    val statistics = _statistics.asStateFlow()

    val filteredAsteroids = combine(
        uiState,
        searchQuery,
        onlyHazardous,
        sortOption
    ) { state, query, hazardous, sort ->
        (state as? AsteroidsUiState.Success)?.let {
            filterAndSortAsteroids(it.asteroids, query, hazardous, sort)
        }.orEmpty()
    }

    private val apiKey = BuildConfig.NASA_API_KEY

    init {
        loadAsteroids()
    }

    /** Actualiza o expone el estado requerido por la pantalla. */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /** Actualiza o expone el estado requerido por la pantalla. */
    fun updateHazardousFilter(enabled: Boolean) {
        _onlyHazardous.value = enabled
    }

    /** Actualiza o expone el estado requerido por la pantalla. */
    fun updateSortOption(option: AsteroidSortOption) {
        _sortOption.value = option
    }

    /** Actualiza o expone el estado requerido por la pantalla. */
    fun updateDateRange(start: String, end: String) {
        _startDate.value = start
        _endDate.value = end
    }

    /** Actualiza o expone el estado requerido por la pantalla. */
    fun loadAsteroids(
        start: String = _startDate.value,
        end: String = _endDate.value
    ) {
        viewModelScope.launch {
            _uiState.value = AsteroidsUiState.Loading

            _startDate.value = start
            _endDate.value = end

            val result = repository.getAsteroids(
                startDate = start,
                endDate = end,
                apiKey = apiKey
            )

            result.fold(
                onSuccess = { response ->
                    val asteroids = response.nearEarthObjects.values.flatten()
                    _statistics.value = calculateAsteroidStatistics(asteroids)
                    _uiState.value = AsteroidsUiState.Success(asteroids)
                },
                onFailure = { error ->
                    if (error is CancellationException) throw error
                    _uiState.value = AsteroidsUiState.Error(
                        message = error.message ?: "No se pudieron cargar los asteroides.",
                        title = (error as? NeoWsApiException)?.title
                            ?: "No pudimos cargar los asteroides",
                        httpStatusCode = (error as? NeoWsApiException)?.statusCode
                    )
                }
            )
        }
    }

    private companion object {
        /** Actualiza o expone el estado requerido por la pantalla. */
        fun today(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }
}
