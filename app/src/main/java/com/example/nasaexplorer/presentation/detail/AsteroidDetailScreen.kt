package com.example.nasaexplorer.presentation.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nasaexplorer.data.remote.neows.AsteroidDto

@Composable
fun AsteroidDetailScreen(
    asteroid: AsteroidDto,
    onBack: () -> Unit
) {
    val approach = asteroid.closeApproachData.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Button(onClick = onBack) {
            Text("Volver")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = asteroid.name ?: "Asteroide desconocido",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        DetailItem(
            title = "Identificador NASA",
            value = asteroid.id ?: "No disponible"
        )

        DetailItem(
            title = "Diámetro mínimo",
            value = "${asteroid.estimatedDiameter?.meters?.minimum ?: 0.0} metros"
        )

        DetailItem(
            title = "Diámetro máximo",
            value = "${asteroid.estimatedDiameter?.meters?.maximum ?: 0.0} metros"
        )

        DetailItem(
            title = "Velocidad relativa",
            value = "${approach?.relativeVelocity?.kilometersPerHour ?: "No disponible"} km/h"
        )

        DetailItem(
            title = "Distancia de aproximación",
            value = "${approach?.missDistance?.kilometers ?: "No disponible"} km"
        )

        DetailItem(
            title = "Fecha de aproximación",
            value = approach?.date ?: "No disponible"
        )

        DetailItem(
            title = "Potencialmente peligroso",
            value = if (asteroid.isPotentiallyHazardous) "Sí" else "No"
        )
    }
}

@Composable
private fun DetailItem(
    title: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )

        HorizontalDivider()
    }
}
