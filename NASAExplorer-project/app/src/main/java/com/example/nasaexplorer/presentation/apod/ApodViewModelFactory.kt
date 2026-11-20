/** Construye el ViewModel correspondiente con sus dependencias. */
package com.example.nasaexplorer.presentation.apod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nasaexplorer.data.repository.ApodRepository

/** Crea el ViewModel proporcionando las dependencias necesarias. */
class ApodViewModelFactory(
    private val repository: ApodRepository
) : ViewModelProvider.Factory {

    /** Actualiza o expone el estado requerido por la pantalla. */
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(ApodViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ApodViewModel(repository) as T
        }

        throw IllegalArgumentException("ViewModel desconocido")
    }
}
