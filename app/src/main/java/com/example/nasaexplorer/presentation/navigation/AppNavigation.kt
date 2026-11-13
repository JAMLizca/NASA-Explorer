package com.example.nasaexplorer.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.nasaexplorer.data.remote.apod.ApodDto
import com.example.nasaexplorer.data.remote.neows.AsteroidDto
import com.example.nasaexplorer.presentation.apod.ApodScreen
import com.example.nasaexplorer.presentation.detail.ApodDetailScreen
import com.example.nasaexplorer.presentation.detail.AsteroidDetailScreen
import com.example.nasaexplorer.presentation.favorites.FavoritesScreen
import com.example.nasaexplorer.presentation.home.HomeScreen
import com.example.nasaexplorer.presentation.neows.AsteroidsScreen
import com.example.nasaexplorer.presentation.settings.SettingsScreen
import com.example.nasaexplorer.presentation.settings.ThemeViewModel

private object Routes {
    const val HOME = "home"
    const val APOD = "apod"
    const val ASTEROIDS = "asteroids"
    const val FAVORITES = "favorites"
    const val SETTINGS = "settings"
    const val ASTEROID_DETAIL = "asteroid_detail"
    const val APOD_DETAIL = "apod_detail"
}

@Composable
fun AppNavigation(themeViewModel: ThemeViewModel) {

    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var selectedAsteroid by remember { mutableStateOf<AsteroidDto?>(null) }
    var selectedApod by remember { mutableStateOf<ApodDto?>(null) }

    val showBottomBar = currentRoute != Routes.ASTEROID_DETAIL && currentRoute != Routes.APOD_DETAIL

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {

                    NavigationBarItem(
                        selected = currentRoute == Routes.HOME,
                        onClick = {
                            navController.navigate(Routes.HOME) {
                                launchSingleTop = true
                            }
                        },
                        icon = { Text("⌂") },
                        label = { Text("Inicio") }
                    )

                    NavigationBarItem(
                        selected = currentRoute == Routes.APOD,
                        onClick = {
                            navController.navigate(Routes.APOD) {
                                launchSingleTop = true
                            }
                        },
                        icon = { Text("✦") },
                        label = { Text("APOD") }
                    )

                    NavigationBarItem(
                        selected = currentRoute == Routes.ASTEROIDS,
                        onClick = {
                            navController.navigate(Routes.ASTEROIDS) {
                                launchSingleTop = true
                            }
                        },
                        icon = { Text("☄") },
                        label = { Text("Asteroides") }
                    )

                    NavigationBarItem(
                        selected = currentRoute == Routes.FAVORITES,
                        onClick = {
                            navController.navigate(Routes.FAVORITES) {
                                launchSingleTop = true
                            }
                        },
                        icon = { Text("♥") },
                        label = { Text("Favoritos") }
                    )

                    NavigationBarItem(
                        selected = currentRoute == Routes.SETTINGS,
                        onClick = {
                            navController.navigate(Routes.SETTINGS) {
                                launchSingleTop = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Ajustes"
                            )
                        },
                        label = { Text("Ajustes") }
                    )
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(Routes.HOME) {
                HomeScreen()
            }

            composable(Routes.APOD) {
                ApodScreen(
                    onApodClick = { apod ->
                        selectedApod = apod
                        navController.navigate(Routes.APOD_DETAIL)
                    }
                )
            }

            composable(Routes.APOD_DETAIL) {
                selectedApod?.let { apod ->
                    ApodDetailScreen(
                        apod = apod,
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }

            composable(Routes.ASTEROIDS) {
                AsteroidsScreen(
                    onAsteroidClick = { asteroid ->
                        selectedAsteroid = asteroid
                        navController.navigate(Routes.ASTEROID_DETAIL)
                    }
                )
            }

            composable(Routes.ASTEROID_DETAIL) {
                selectedAsteroid?.let { asteroid ->
                    AsteroidDetailScreen(
                        asteroid = asteroid,
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }

            composable(Routes.FAVORITES) {
                FavoritesScreen()
            }

            composable(Routes.SETTINGS) {
                SettingsScreen(themeViewModel = themeViewModel)
            }
        }
    }
}
