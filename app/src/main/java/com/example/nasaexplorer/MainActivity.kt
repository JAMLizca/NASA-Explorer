/** Actúa como punto de entrada de la aplicación Android y hospeda la interfaz. */
package com.example.nasaexplorer

import android.os.Bundle
import androidx.compose.ui.platform.LocalConfiguration
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.CompositionLocalProvider
import java.util.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nasaexplorer.data.preferences.UserPreferencesRepository
import com.example.nasaexplorer.presentation.navigation.AppNavigation
import com.example.nasaexplorer.presentation.settings.ThemeViewModel
import com.example.nasaexplorer.presentation.settings.ThemeViewModelFactory
import com.example.nasaexplorer.ui.theme.NASAExplorerTheme

/** Inicializa la actividad principal y monta la navegación de Compose. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current.applicationContext

            val repository = remember {
                UserPreferencesRepository(context)
            }

            val factory = remember {
                ThemeViewModelFactory(repository)
            }

            val themeViewModel: ThemeViewModel = viewModel(
                factory = factory
            )

            val themeMode by themeViewModel.themeMode.collectAsState()
            val appLanguage by themeViewModel.appLanguage.collectAsState()

            val systemDarkTheme = isSystemInDarkTheme()
            val localizedConfiguration = remember(appLanguage, context.resources.configuration) {
                android.content.res.Configuration(context.resources.configuration).apply {
                    setLocale(Locale.forLanguageTag(appLanguage))
                }
            }

            val darkTheme = when (themeMode) {
                "LIGHT" -> false
                "DARK" -> true
                else -> systemDarkTheme
            }

            CompositionLocalProvider(
                LocalConfiguration provides localizedConfiguration
            ) {
                NASAExplorerTheme(darkTheme = darkTheme) {
                    AppNavigation(themeViewModel = themeViewModel)
                }
            }
        }
    }
}

@Composable
/** Dibuja en Compose la interfaz asociada a greeting */
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
/** Dibuja en Compose la interfaz asociada a greeting preview */
fun GreetingPreview() {
    NASAExplorerTheme {
        Greeting("Android")
    }
}
