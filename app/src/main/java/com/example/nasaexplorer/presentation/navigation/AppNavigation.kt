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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.nasaexplorer.presentation.apod.ApodScreen
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
}

@Composable
fun AppNavigation(themeViewModel: ThemeViewModel) {

    val navController = rememberNavController()

    val navBackStackEntry by
    navController.currentBackStackEntryAsState()

    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
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
                ApodScreen()
            }

            composable(Routes.ASTEROIDS) {
                AsteroidsScreen()
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


