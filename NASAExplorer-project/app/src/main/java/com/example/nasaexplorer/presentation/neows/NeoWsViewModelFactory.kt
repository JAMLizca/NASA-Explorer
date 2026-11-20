/** Construye el ViewModel correspondiente con sus dependencias. */
package com.example.nasaexplorer.presentation.neows

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nasaexplorer.data.repository.NeoWsRepository

/** Crea el ViewModel proporcionando las dependencias necesarias. */
class NeoWsViewModelFactory(
    private val repository: NeoWsRepository
) : ViewModelProvider.Factory {

    /** Actualiza o expone el estado requerido por la pantalla. */
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(NeoWsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NeoWsViewModel(repository) as T
        }

        throw IllegalArgumentException("ViewModel desconocido")
    }
}
