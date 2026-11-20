/** Gestiona el estado y coordina las operaciones de su pantalla. */
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

/** Define el contrato de asteroids ui state. */
sealed interface AsteroidsUiState {
    data object Loading : AsteroidsUiState
    data class Success(val asteroids: List<AsteroidDto>) : AsteroidsUiState
    /** Encapsula la responsabilidad principal de error. */
    data class Error(
        val message: String,
        val title: String = "No pudimos cargar los asteroides",
        val httpStatusCode: Int? = null
    ) : AsteroidsUiState
}

/** Mantiene y transforma el estado requerido por la pantalla de neo ws. */
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

    /** Ejecuta o expone la operación de estado asociada a update search query. */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateHazardousFilter(enabled: Boolean) {
        _onlyHazardous.value = enabled
    }

    /** Ejecuta o expone la operación de estado asociada a update sort option. */
    fun updateSortOption(option: AsteroidSortOption) {
        _sortOption.value = option
    }

    fun updateDateRange(start: String, end: String) {
        _startDate.value = start
        _endDate.value = end
    }

    /** Ejecuta o expone la operación de estado asociada a load asteroids. */
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
        /** Ejecuta o expone la operación de estado asociada a today. */
        fun today(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }
}
