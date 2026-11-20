/** Crea el ViewModel correspondiente proporcionando sus dependencias. */
package com.example.nasaexplorer.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nasaexplorer.data.repository.NasaAsteroidImageRepository

/** Construye una instancia del ViewModel con sus dependencias necesarias. */
class AsteroidImageViewModelFactory(
    private val repository: NasaAsteroidImageRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AsteroidImageViewModel::class.java)) {
            return AsteroidImageViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
