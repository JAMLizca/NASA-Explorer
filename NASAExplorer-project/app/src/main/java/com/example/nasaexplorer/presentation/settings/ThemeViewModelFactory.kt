/** Construye el ViewModel correspondiente con sus dependencias. */
package com.example.nasaexplorer.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nasaexplorer.data.preferences.UserPreferencesRepository

/** Crea el ViewModel proporcionando las dependencias necesarias. */
class ThemeViewModelFactory(
    private val repository: UserPreferencesRepository
) : ViewModelProvider.Factory {

    /** Actualiza o expone el estado requerido por la pantalla. */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ThemeViewModel(repository) as T
        }

        throw IllegalArgumentException("ViewModel desconocido")
    }
}
