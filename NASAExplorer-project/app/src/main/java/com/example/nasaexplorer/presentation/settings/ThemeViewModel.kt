/** Administra el estado y coordina las operaciones de su pantalla. */
package com.example.nasaexplorer.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nasaexplorer.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Mantiene el estado de la pantalla y coordina sus operaciones. */
class ThemeViewModel(
    private val repository: UserPreferencesRepository
) : ViewModel() {

    val themeMode = repository.themeMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "SYSTEM"
    )

    val appLanguage = repository.appLanguage.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "es"
    )

    /** Actualiza o expone el estado requerido por la pantalla. */
    fun changeTheme(mode: String) {
        viewModelScope.launch {
            repository.saveThemeMode(mode)
        }
    }

    /** Actualiza o expone el estado requerido por la pantalla. */
    fun changeLanguage(language: String) {
        viewModelScope.launch {
            repository.saveAppLanguage(language)
        }
    }
}
