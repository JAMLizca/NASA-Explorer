/** Construye el ViewModel correspondiente con sus dependencias. */
package com.example.nasaexplorer.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nasaexplorer.data.repository.NasaAsteroidImageRepository

/** Crea el ViewModel proporcionando las dependencias necesarias. */
class AsteroidImageViewModelFactory(
    private val repository: NasaAsteroidImageRepository
) : ViewModelProvider.Factory {
    /** Actualiza o expone el estado requerido por la pantalla. */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AsteroidImageViewModel::class.java)) {
            return AsteroidImageViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
