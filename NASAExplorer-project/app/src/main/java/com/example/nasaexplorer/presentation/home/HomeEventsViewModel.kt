/** Administra el estado y coordina las operaciones de su pantalla. */
package com.example.nasaexplorer.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nasaexplorer.data.remote.donki.DonkiNotificationDto
import com.example.nasaexplorer.data.remote.eonet.EonetEventDto
import com.example.nasaexplorer.data.repository.DonkiRepository
import com.example.nasaexplorer.data.repository.EonetRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Define el contrato que deben cumplir las implementaciones relacionadas. */
sealed interface FeedUiState<out T> {
    /** Expone una instancia compartida para esta responsabilidad. */
    data object Loading : FeedUiState<Nothing>
    /** Encapsula la responsabilidad principal de esta sección. */
    data class Success<T>(val items: List<T>) : FeedUiState<T>
    /** Encapsula la responsabilidad principal de esta sección. */
    data class Error(val message: String) : FeedUiState<Nothing>
}

/** Mantiene el estado de la pantalla y coordina sus operaciones. */
class HomeEventsViewModel(
    private val donkiRepository: DonkiRepository,
    private val eonetRepository: EonetRepository,
    private val nasaApiKey: String
) : ViewModel() {
    private val _donkiState =
        MutableStateFlow<FeedUiState<DonkiNotificationDto>>(FeedUiState.Loading)
    val donkiState: StateFlow<FeedUiState<DonkiNotificationDto>> = _donkiState.asStateFlow()

    private val _eonetState =
        MutableStateFlow<FeedUiState<EonetEventDto>>(FeedUiState.Loading)
    val eonetState: StateFlow<FeedUiState<EonetEventDto>> = _eonetState.asStateFlow()

    init {
        loadDonki()
        loadEonet()
    }

    /** Actualiza o expone el estado requerido por la pantalla. */
    fun loadDonki() {
        viewModelScope.launch {
            _donkiState.value = FeedUiState.Loading
            _donkiState.value = donkiRepository.getRecentNotifications(nasaApiKey).toFeedState()
        }
    }

    /** Actualiza o expone el estado requerido por la pantalla. */
    fun loadEonet() {
        viewModelScope.launch {
            _eonetState.value = FeedUiState.Loading
            _eonetState.value = eonetRepository.getOpenEvents().toFeedState()
        }
    }

    private fun <T> Result<List<T>>.toFeedState(): FeedUiState<T> = fold(
        onSuccess = { FeedUiState.Success(it) },
        onFailure = {
            if (it is CancellationException) throw it
            FeedUiState.Error(it.message ?: "No se pudo cargar la información.")
        }
    )
}
