/** Construye el ViewModel correspondiente con sus dependencias. */
package com.example.nasaexplorer.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nasaexplorer.data.repository.FavoriteRepository

/** Crea el ViewModel proporcionando las dependencias necesarias. */
class FavoritesViewModelFactory(
    private val repository: FavoriteRepository
) : ViewModelProvider.Factory {

    /** Actualiza o expone el estado requerido por la pantalla. */
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoritesViewModel(repository) as T
        }

        throw IllegalArgumentException("ViewModel desconocido")
    }
}
