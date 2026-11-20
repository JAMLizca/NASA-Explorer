/** Crea el ViewModel correspondiente proporcionando sus dependencias. */
package com.example.nasaexplorer.presentation.neows

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nasaexplorer.data.repository.NeoWsRepository

/** Construye una instancia del ViewModel con sus dependencias necesarias. */
class NeoWsViewModelFactory(
    private val repository: NeoWsRepository
) : ViewModelProvider.Factory {

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
