/** Define la navegación entre las pantallas de la aplicación. */
package com.example.nasaexplorer.presentation.navigation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import com.example.nasaexplorer.ui.components.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.selection.selectable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.nasaexplorer.data.remote.apod.ApodDto
import com.example.nasaexplorer.data.local.entity.FavoriteEntity
import com.example.nasaexplorer.data.remote.neows.AsteroidDto
import com.example.nasaexplorer.R
import com.example.nasaexplorer.presentation.apod.ApodScreen
import com.example.nasaexplorer.presentation.assistant.AssistantDialog
import com.example.nasaexplorer.presentation.detail.ApodDetailScreen
import com.example.nasaexplorer.presentation.detail.AsteroidDetailScreen
import com.example.nasaexplorer.presentation.favorites.FavoritesScreen
import com.example.nasaexplorer.presentation.favorites.FavoriteDetailScreen
import com.example.nasaexplorer.presentation.home.HomeScreen
import com.example.nasaexplorer.presentation.eonet.EonetMapScreen
import com.example.nasaexplorer.presentation.neows.AsteroidsScreen
import com.example.nasaexplorer.presentation.settings.SettingsScreen
import com.example.nasaexplorer.presentation.settings.ThemeViewModel
import com.example.nasaexplorer.ui.components.localizedString
import androidx.compose.ui.graphics.vector.ImageVector

private object Routes {
    const val HOME = "home"
    const val APOD = "apod"
    const val ASTEROIDS = "asteroids"
    const val FAVORITES = "favorites"
    const val SETTINGS = "settings"
    const val ASTEROID_DETAIL = "asteroid_detail"
    const val APOD_DETAIL = "apod_detail"
    const val FAVORITE_DETAIL = "favorite_detail"
    const val EONET_MAP = "eonet_map"
}

@Composable
/** Dibuja en Compose la interfaz asociada a app navigation */
fun AppNavigation(themeViewModel: ThemeViewModel) {

    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var selectedAsteroid by remember { mutableStateOf<AsteroidDto?>(null) }
    var selectedApod by remember { mutableStateOf<ApodDto?>(null) }
    var selectedFavorite by remember { mutableStateOf<FavoriteEntity?>(null) }
    var showAssistant by remember { mutableStateOf(false) }

    val showBottomBar = currentRoute != Routes.ASTEROID_DETAIL &&
        currentRoute != Routes.APOD_DETAIL &&
        currentRoute != Routes.FAVORITE_DETAIL &&
        currentRoute != Routes.EONET_MAP
    val assistantAvailable = currentRoute in setOf(
        Routes.HOME,
        Routes.APOD,
        Routes.ASTEROIDS,
        Routes.APOD_DETAIL,
        Routes.ASTEROID_DETAIL
    )

    LaunchedEffect(assistantAvailable) {
        if (!assistantAvailable) showAssistant = false
    }

    Scaffold(
        floatingActionButton = {
            if (assistantAvailable) {
                FloatingActionButton(
                    onClick = { showAssistant = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Image(
                        painter = painterResource(R.mipmap.nasa_explorer_launcher),
                        contentDescription = localizedString("Abrir asistente NASA"),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(50))
                    )
                }
            }
        },
        bottomBar = {
            if (showBottomBar) {
                ExplorerNavigationBar(
                    currentRoute = currentRoute,
                    onNavigate = navController::navigateTopLevel
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
                HomeScreen(
                    onApodClick = { navController.navigateTopLevel(Routes.APOD) },
                    onAsteroidsClick = { navController.navigateTopLevel(Routes.ASTEROIDS) },
                    onFavoritesClick = { navController.navigateTopLevel(Routes.FAVORITES) },
                    onEonetMapClick = { navController.navigate(Routes.EONET_MAP) }
                )
            }

            composable(Routes.EONET_MAP) {
                EonetMapScreen(onBack = { navController.popBackStack() })
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
                FavoritesScreen(
                    onFavoriteClick = { favorite ->
                        selectedFavorite = favorite
                        navController.navigate(Routes.FAVORITE_DETAIL)
                    }
                )
            }

            composable(Routes.FAVORITE_DETAIL) {
                selectedFavorite?.let { favorite ->
                    FavoriteDetailScreen(
                        favorite = favorite,
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            composable(Routes.SETTINGS) {
                SettingsScreen(themeViewModel = themeViewModel)
            }
        }
    }

    if (showAssistant && assistantAvailable) {
        val context = when {
            currentRoute == Routes.APOD_DETAIL && selectedApod != null -> {
                val apod = selectedApod!!
                mapOf(
                    "id" to "apod-${apod.date.orEmpty()}",
                    "type" to "APOD",
                    "title" to apod.title.orEmpty(),
                    "date" to apod.date.orEmpty(),
                    "media_type" to apod.mediaType.orEmpty(),
                    "explanation" to apod.explanation.orEmpty(),
                    "credit" to (apod.credit ?: apod.copyright.orEmpty())
                )
            }
            currentRoute == Routes.ASTEROID_DETAIL && selectedAsteroid != null -> {
                val asteroid = selectedAsteroid!!
                val approach = asteroid.closeApproachData.firstOrNull()
                mapOf(
                    "id" to "neo-${asteroid.id.orEmpty()}",
                    "type" to "ASTEROID",
                    "name" to asteroid.name.orEmpty(),
                    "nasa_id" to asteroid.id.orEmpty(),
                    "diameter_min_meters" to
                        (asteroid.estimatedDiameter?.meters?.minimum?.toString() ?: "No disponible"),
                    "diameter_max_meters" to
                        (asteroid.estimatedDiameter?.meters?.maximum?.toString() ?: "No disponible"),
                    "relative_velocity_kmh" to
                        (approach?.relativeVelocity?.kilometersPerHour ?: "No disponible"),
                    "miss_distance_km" to (approach?.missDistance?.kilometers ?: "No disponible"),
                    "approach_date" to (approach?.date ?: "No disponible"),
                    "potentially_hazardous" to
                        (asteroid.isPotentiallyHazardous?.toString() ?: "No disponible")
                )
            }
            currentRoute == Routes.APOD -> mapOf(
                "type" to "APOD",
                "scope" to "La pantalla muestra el módulo APOD, pero aún no se ha abierto una publicación específica. Se pueden responder preguntas generales sobre APOD."
            )
            currentRoute == Routes.ASTEROIDS -> mapOf(
                "type" to "NEOWS",
                "scope" to "La pantalla muestra el feed de asteroides NeoWs, pero aún no se ha abierto un asteroide específico. Se pueden explicar conceptos y términos generales sobre asteroides."
            )
            currentRoute == Routes.HOME -> mapOf(
                "type" to "GENERAL",
                "scope" to "La pantalla actual es Inicio. No hay un objeto específico seleccionado. Responde normalmente las preguntas generales de astronomía."
            )
            else -> mapOf(
                "type" to "GENERAL",
                "scope" to "Responde normalmente las preguntas generales de astronomía. Si la pregunta requiere datos NASA de un objeto concreto que no aparece en el contexto, indica brevemente qué objeto o pantalla debe seleccionar."
            )
        }
        AssistantDialog(
            context = context,
            onDismiss = { showAssistant = false }
        )
    }

}

@Composable
private fun ExplorerNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val destinations = listOf(
        NavigationDestination(
            route = Routes.HOME,
            label = "Inicio",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        NavigationDestination(
            route = Routes.APOD,
            label = "APOD",
            selectedIcon = Icons.Filled.PhotoCamera,
            unselectedIcon = Icons.Outlined.PhotoCamera
        ),
        NavigationDestination(
            route = Routes.ASTEROIDS,
            label = "Asteroides",
            selectedIcon = Icons.Filled.Public,
            unselectedIcon = Icons.Outlined.Public
        ),
        NavigationDestination(
            route = Routes.FAVORITES,
            label = "Favoritos",
            selectedIcon = Icons.Filled.Favorite,
            unselectedIcon = Icons.Outlined.FavoriteBorder
        ),
        NavigationDestination(
            route = Routes.SETTINGS,
            label = "Ajustes",
            selectedIcon = Icons.Filled.Tune,
            unselectedIcon = Icons.Default.Settings
        )
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        shadowElevation = 8.dp,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            destinations.forEach { destination ->
                val selected = currentRoute == destination.route
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .selectable(
                            selected = selected,
                            role = Role.Tab,
                            onClick = { onNavigate(destination.route) }
                        )
                        .padding(vertical = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(13.dp))
                            .backgroundIfSelected(selected)
                            .animateContentSize()
                            .padding(horizontal = 13.dp, vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (selected) {
                                destination.selectedIcon
                            } else {
                                destination.unselectedIcon
                            },
                            contentDescription = null,
                            tint = if (selected) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.size(21.dp)
                        )
                    }
                    Text(
                        text = destination.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        fontWeight = if (selected) {
                            androidx.compose.ui.text.font.FontWeight.SemiBold
                        } else {
                            androidx.compose.ui.text.font.FontWeight.Medium
                        },
                        maxLines = 1
                    )
                }
            }
        }
    }
}

private data class NavigationDestination(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
private fun Modifier.backgroundIfSelected(selected: Boolean): Modifier =
    if (selected) {
        background(MaterialTheme.colorScheme.primaryContainer)
    } else {
        this
    }

private fun androidx.navigation.NavHostController.navigateTopLevel(route: String) {
    navigate(route) {
        popUpTo(graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
