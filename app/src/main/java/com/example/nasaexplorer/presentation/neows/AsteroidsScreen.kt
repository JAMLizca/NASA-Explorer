package com.example.nasaexplorer.presentation.neows

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nasaexplorer.data.local.AppDatabase
import com.example.nasaexplorer.data.local.entity.FavoriteEntity
import com.example.nasaexplorer.data.remote.neows.AsteroidDto
import com.example.nasaexplorer.data.repository.FavoriteRepository
import com.example.nasaexplorer.data.repository.NeoWsRepository
import kotlinx.coroutines.launch

@Composable
fun AsteroidsScreen(
    onAsteroidClick: (AsteroidDto) -> Unit = {}
) {
    val context = LocalContext.current
    val repository = remember { NeoWsRepository() }
    val factory = remember {
        NeoWsViewModelFactory(repository)
    }

    val favoriteRepository = remember {
        FavoriteRepository(
            AppDatabase.getDatabase(context).favoriteDao()
        )
    }

    val asteroidViewModel: NeoWsViewModel = viewModel(
        factory = factory
    )

    val uiState by asteroidViewModel.uiState.collectAsState()
    val searchQuery by asteroidViewModel.searchQuery.collectAsState()
    val onlyHazardous by asteroidViewModel.onlyHazardous.collectAsState()
    val sortOption by asteroidViewModel.sortOption.collectAsState()

    var sortMenuExpanded by remember {
        mutableStateOf(false)
    }

    val filteredAsteroids by asteroidViewModel.filteredAsteroids.collectAsState(
        initial = emptyList()
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Asteroides cercanos",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Objetos cercanos a la Tierra registrados por NASA.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (val state = uiState) {

            is AsteroidsUiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Text("Cargando asteroides...")
                }
            }

            is AsteroidsUiState.Success -> {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        asteroidViewModel.updateSearchQuery(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Buscar asteroide")
                    },
                    placeholder = {
                        Text("Escribe el nombre...")
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = onlyHazardous,
                        onCheckedChange = {
                            asteroidViewModel.updateHazardousFilter(it)
                        }
                    )

                    Text("Solo potencialmente peligrosos")
                }

                Spacer(modifier = Modifier.height(4.dp))

                Box {
                    TextButton(
                        onClick = {
                            sortMenuExpanded = true
                        }
                    ) {
                        Text(
                            text = "Ordenar: ${
                                when (sortOption) {
                                    AsteroidSortOption.NAME -> "Nombre"
                                    AsteroidSortOption.DIAMETER -> "Diámetro"
                                    AsteroidSortOption.SPEED -> "Velocidad"
                                    AsteroidSortOption.DISTANCE -> "Distancia"
                                }
                            }"
                        )
                    }

                    DropdownMenu(
                        expanded = sortMenuExpanded,
                        onDismissRequest = {
                            sortMenuExpanded = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Nombre") },
                            onClick = {
                                asteroidViewModel.updateSortOption(
                                    AsteroidSortOption.NAME
                                )
                                sortMenuExpanded = false
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Mayor diámetro") },
                            onClick = {
                                asteroidViewModel.updateSortOption(
                                    AsteroidSortOption.DIAMETER
                                )
                                sortMenuExpanded = false
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Mayor velocidad") },
                            onClick = {
                                asteroidViewModel.updateSortOption(
                                    AsteroidSortOption.SPEED
                                )
                                sortMenuExpanded = false
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Menor distancia") },
                            onClick = {
                                asteroidViewModel.updateSortOption(
                                    AsteroidSortOption.DISTANCE
                                )
                                sortMenuExpanded = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (filteredAsteroids.isEmpty()) {
                    Text(
                        text = "No se encontraron asteroides.",
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "Asteroides encontrados: ${filteredAsteroids.size}",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(
                            items = filteredAsteroids,
                            key = { it.id ?: it.name.orEmpty() }
                        ) { asteroid ->
                            AsteroidCard(
                                asteroid = asteroid,
                                favoriteRepository = favoriteRepository,
                                onClick = { onAsteroidClick(asteroid) }
                            )
                        }
                    }
                }
            }

            is AsteroidsUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudOff,
                        contentDescription = "Sin conexión",
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.error
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "No pudimos cargar los asteroides",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { asteroidViewModel.loadAsteroids() }
                    ) {
                        Text("Intentar nuevamente")
                    }
                }
            }
        }
    }
}

@Composable
private fun AsteroidCard(
    asteroid: AsteroidDto,
    favoriteRepository: FavoriteRepository,
    onClick: () -> Unit
) {
    val approach = asteroid.closeApproachData.firstOrNull()

    val minimumDiameter =
        asteroid.estimatedDiameter?.meters?.minimum

    val maximumDiameter =
        asteroid.estimatedDiameter?.meters?.maximum

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = asteroid.name ?: "Asteroide sin nombre",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

                AsteroidFavoriteButton(
                    asteroid = asteroid,
                    favoriteRepository = favoriteRepository
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Diámetro estimado: ${
                    minimumDiameter?.let { "%.2f".format(it) } ?: "N/D"
                } - ${
                    maximumDiameter?.let { "%.2f".format(it) } ?: "N/D"
                } metros"
            )

            Text(
                text = "Velocidad: ${
                    approach?.relativeVelocity?.kilometersPerHour ?: "N/D"
                } km/h"
            )

            Text(
                text = "Distancia: ${
                    approach?.missDistance?.kilometers ?: "N/D"
                } km"
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (asteroid.isPotentiallyHazardous) {
                    "Clasificación: potencialmente peligroso"
                } else {
                    "Clasificación: no clasificado como potencialmente peligroso"
                },
                color = if (asteroid.isPotentiallyHazardous) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun AsteroidFavoriteButton(
    asteroid: AsteroidDto,
    favoriteRepository: FavoriteRepository
) {
    val asteroidId = "neo_${asteroid.id ?: asteroid.name}"

    val isFavorite by favoriteRepository
        .isFavorite(asteroidId)
        .collectAsState(initial = false)

    val coroutineScope = rememberCoroutineScope()

    IconButton(
        onClick = {
            coroutineScope.launch {
                if (isFavorite) {
                    favoriteRepository.deleteFavorite(asteroidId)
                } else {
                    val approach = asteroid.closeApproachData.firstOrNull()

                    val favorite = FavoriteEntity(
                        id = asteroidId,
                        title = asteroid.name ?: "Asteroide desconocido",
                        description = """
                            Diámetro mínimo: ${
                                asteroid.estimatedDiameter?.meters?.minimum ?: 0.0
                            } metros.
                            Diámetro máximo: ${
                                asteroid.estimatedDiameter?.meters?.maximum ?: 0.0
                            } metros.
                            Velocidad: ${
                                approach?.relativeVelocity?.kilometersPerHour ?: "No disponible"
                            } km/h.
                            Distancia: ${
                                approach?.missDistance?.kilometers ?: "No disponible"
                            } km.
                            Potencialmente peligroso: ${
                                asteroid.isPotentiallyHazardous
                            }.
                        """.trimIndent(),
                        imageUrl = "",
                        date = approach?.date ?: "",
                        type = "ASTEROID"
                    )

                    favoriteRepository.insertFavorite(favorite)
                }
            }
        }
    ) {
        Icon(
            imageVector = if (isFavorite) {
                Icons.Filled.Favorite
            } else {
                Icons.Filled.FavoriteBorder
            },
            contentDescription = if (isFavorite) {
                "Quitar de favoritos"
            } else {
                "Agregar a favoritos"
            },
            tint = if (isFavorite) {
                Color.Red
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}
