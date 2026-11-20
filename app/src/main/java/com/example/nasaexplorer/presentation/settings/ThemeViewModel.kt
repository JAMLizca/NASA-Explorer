/** Gestiona el estado y coordina las operaciones de su pantalla. */
package com.example.nasaexplorer.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nasaexplorer.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Mantiene y transforma el estado requerido por la pantalla de theme. */
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

    /** Ejecuta o expone la operación de estado asociada a change theme. */
    fun changeTheme(mode: String) {
        viewModelScope.launch {
            repository.saveThemeMode(mode)
        }
    }

    /** Ejecuta o expone la operación de estado asociada a change language. */
    fun changeLanguage(language: String) {
        viewModelScope.launch {
            repository.saveAppLanguage(language)
        }
    }
}
