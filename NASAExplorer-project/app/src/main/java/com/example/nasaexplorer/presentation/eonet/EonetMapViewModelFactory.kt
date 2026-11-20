/** Crea el ViewModel de EONET con el repositorio de la aplicación. */
package com.example.nasaexplorer.presentation.eonet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nasaexplorer.data.repository.EonetRepository

class EonetMapViewModelFactory(
    private val repository: EonetRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(EonetMapViewModel::class.java))
        return EonetMapViewModel(repository) as T
    }
}
