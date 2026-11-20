/** Administra el estado y coordina las operaciones de su pantalla. */
package com.example.nasaexplorer.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nasaexplorer.data.local.entity.FavoriteEntity
import com.example.nasaexplorer.data.repository.FavoriteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Mantiene el estado de la pantalla y coordina sus operaciones. */
class FavoritesViewModel(
    private val repository: FavoriteRepository
) : ViewModel() {

    val favorites: StateFlow<List<FavoriteEntity>> =
        repository.getAllFavorites().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /** Actualiza o expone el estado requerido por la pantalla. */
    fun addFavorite(favorite: FavoriteEntity) {
        viewModelScope.launch {
            repository.insertFavorite(favorite)
        }
    }

    /** Actualiza o expone el estado requerido por la pantalla. */
    fun removeFavorite(id: String) {
        viewModelScope.launch {
            repository.deleteFavorite(id)
        }
    }

    /** Actualiza o expone el estado requerido por la pantalla. */
    fun removeAllFavorites() {
        viewModelScope.launch {
            repository.deleteAllFavorites()
        }
    }
}
