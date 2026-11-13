package com.example.nasaexplorer.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nasaexplorer.data.local.entity.FavoriteEntity
import com.example.nasaexplorer.data.repository.FavoriteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val repository: FavoriteRepository
) : ViewModel() {

    val favorites: StateFlow<List<FavoriteEntity>> =
        repository.getAllFavorites().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addFavorite(favorite: FavoriteEntity) {
        viewModelScope.launch {
            repository.insertFavorite(favorite)
        }
    }

    fun removeFavorite(id: String) {
        viewModelScope.launch {
            repository.deleteFavorite(id)
        }
    }

    fun removeAllFavorites() {
        viewModelScope.launch {
            repository.deleteAllFavorites()
        }
    }
}
