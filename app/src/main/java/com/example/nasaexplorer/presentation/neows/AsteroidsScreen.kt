package com.example.nasaexplorer.presentation.neows

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nasaexplorer.data.remote.neows.AsteroidDto
import com.example.nasaexplorer.data.repository.NeoWsRepository

@Composable
fun AsteroidsScreen() {

    val repository = remember { NeoWsRepository() }
    val factory = remember {
        NeoWsViewModelFactory(repository)
    }

    val asteroidViewModel: NeoWsViewModel = viewModel(
        factory = factory
    )

    val uiState by asteroidViewModel.uiState.collectAsState()

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
                if (state.asteroids.isEmpty()) {
                    Text("No se encontraron asteroides para esta fecha.")
                } else {
                    Text(
                        text = "Asteroides encontrados: ${state.asteroids.size}",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(
                            items = state.asteroids,
                            key = { it.id ?: it.name.orEmpty() }
                        ) { asteroid ->
                            AsteroidCard(asteroid)
                        }
                    }
                }
            }

            is AsteroidsUiState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { asteroidViewModel.loadAsteroids() }
                ) {
                    Text("Intentar nuevamente")
                }
            }
        }
    }
}

@Composable
private fun AsteroidCard(asteroid: AsteroidDto) {

    val approach = asteroid.closeApproachData.firstOrNull()

    val minimumDiameter =
        asteroid.estimatedDiameter?.meters?.minimum

    val maximumDiameter =
        asteroid.estimatedDiameter?.meters?.maximum

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = asteroid.name ?: "Asteroide sin nombre",
                style = MaterialTheme.typography.titleMedium
            )

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
