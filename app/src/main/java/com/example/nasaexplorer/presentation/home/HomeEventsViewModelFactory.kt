/** Crea el ViewModel correspondiente proporcionando sus dependencias. */
package com.example.nasaexplorer.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nasaexplorer.data.repository.DonkiRepository
import com.example.nasaexplorer.data.repository.EonetRepository

/** Construye una instancia del ViewModel con sus dependencias necesarias. */
class HomeEventsViewModelFactory(
    private val donkiRepository: DonkiRepository,
    private val eonetRepository: EonetRepository,
    private val nasaApiKey: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeEventsViewModel::class.java)) {
            return HomeEventsViewModel(donkiRepository, eonetRepository, nasaApiKey) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
