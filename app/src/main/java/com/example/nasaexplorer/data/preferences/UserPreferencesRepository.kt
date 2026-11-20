/** Gestiona de forma persistente las preferencias configurables por el usuario. */
package com.example.nasaexplorer.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "user_preferences"
)

/** Centraliza el acceso y manejo de datos para user preferences. */
class UserPreferencesRepository(
    private val context: Context
) {
    private companion object {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val APP_LANGUAGE = stringPreferencesKey("app_language")
    }

    val themeMode: Flow<String> = context.dataStore.data.map { preferences ->
        // Un perfil nuevo conserva el comportamiento esperado: seguir el tema del sistema.
        preferences[THEME_MODE] ?: "SYSTEM"
    }

    val appLanguage: Flow<String> = context.dataStore.data.map { preferences ->
        // La interfaz inicia en español mientras el usuario no elija otro idioma.
        preferences[APP_LANGUAGE] ?: "es"
    }

    suspend fun saveThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = mode
        }
    }

    suspend fun saveAppLanguage(language: String) {
        require(language == "es" || language == "en") {
            "Unsupported app language: $language"
        }
        context.dataStore.edit { preferences ->
            preferences[APP_LANGUAGE] = language
        }
    }
}
