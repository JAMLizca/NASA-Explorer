package com.example.nasaexplorer

import android.os.Bundle
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nasaexplorer.data.preferences.UserPreferencesRepository
import com.example.nasaexplorer.presentation.navigation.AppNavigation
import com.example.nasaexplorer.presentation.settings.ThemeViewModel
import com.example.nasaexplorer.presentation.settings.ThemeViewModelFactory
import com.example.nasaexplorer.ui.theme.NASAExplorerTheme

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

            val systemDarkTheme = isSystemInDarkTheme()

            val darkTheme = when (themeMode) {
                "LIGHT" -> false
                "DARK" -> true
                else -> systemDarkTheme
            }

            NASAExplorerTheme(darkTheme = darkTheme) {
                AppNavigation(themeViewModel = themeViewModel)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NASAExplorerTheme {
        Greeting("Android")
    }
}
