/** Compone la pantalla y conecta la interfaz con su estado. */
package com.example.nasaexplorer.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.nasaexplorer.data.remote.neows.AsteroidDto
import com.example.nasaexplorer.data.remote.images.NasaImageRetrofitInstance
import com.example.nasaexplorer.data.repository.NasaAsteroidImageRepository
import com.example.nasaexplorer.ui.components.Text
import com.example.nasaexplorer.ui.components.localizedString
import java.util.Locale

/** Compone la interfaz asociada a esta sección de la pantalla. */
@Composable
fun AsteroidDetailScreen(
    asteroid: AsteroidDto,
    onBack: () -> Unit
) {
    val approach = asteroid.closeApproachData.firstOrNull()
    val asteroidName = asteroid.name.orEmpty()
    val imageRepository = remember {
        NasaAsteroidImageRepository(NasaImageRetrofitInstance.api)
    }
    val imageViewModel: AsteroidImageViewModel = viewModel(
        factory = remember(imageRepository) {
            AsteroidImageViewModelFactory(imageRepository)
        }
    )
    val imageState by imageViewModel.uiState.collectAsState()
    LaunchedEffect(asteroidName) {
        imageViewModel.load(asteroidName)
    }
    val classificationColor = when (asteroid.isPotentiallyHazardous) {
        true -> MaterialTheme.colorScheme.error
        false -> MaterialTheme.colorScheme.primary
        null -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = localizedString("Volver a asteroides")
                )
            }
            Column(Modifier.weight(1f)) {
                Text(
                    "DETALLE DEL OBJETO",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "NASA · NeoWs",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    "NASA",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsteroidPhotoPanel(
                state = imageState,
                onRetry = { imageViewModel.load(asteroidName, forceRefresh = true) }
            )

            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(
                    "IDENTIFICADOR NASA · ${asteroid.id ?: "No disponible"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    asteroid.name ?: "Asteroide desconocido",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = classificationColor.copy(alpha = 0.10f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (asteroid.isPotentiallyHazardous == true) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = classificationColor
                        )
                    } else {
                        Icon(
                            Icons.Default.Public,
                            contentDescription = null,
                            tint = classificationColor
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            "Clasificación NASA",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            when (asteroid.isPotentiallyHazardous) {
                                true -> "Potencialmente peligroso"
                                false -> "No potencialmente peligroso"
                                null -> "No disponible"
                            },
                            style = MaterialTheme.typography.titleSmall,
                            color = classificationColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Text(
                "Datos estimados",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MeasurementCard(
                    title = "Diámetro mínimo",
                    value = asteroid.estimatedDiameter?.meters?.minimum
                        ?.let { "${"%.2f".format(Locale.US, it)} m" } ?: "No disponible",
                    modifier = Modifier.weight(1f)
                )
                MeasurementCard(
                    title = "Diámetro máximo",
                    value = asteroid.estimatedDiameter?.meters?.maximum
                        ?.let { "${"%.2f".format(Locale.US, it)} m" } ?: "No disponible",
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                "Aproximación registrada",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
                ) {
                    MeasurementRow(
                        title = "Velocidad relativa",
                        value = approach?.relativeVelocity?.kilometersPerHour
                            ?.let { "$it km/h" } ?: "No disponible"
                    )
                    MeasurementRow(
                        title = "Distancia de aproximación",
                        value = approach?.missDistance?.kilometers
                            ?.let { "$it km" } ?: "No disponible"
                    )
                    MeasurementRow(
                        title = "Fecha de aproximación",
                        value = approach?.date ?: "No disponible",
                        isLast = true
                    )
                }
            }

            Text(
                "Solo se muestra una fotografía identificada en la biblioteca de NASA. Si no hay una coincidencia fiable, no se usa una imagen ilustrativa.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(88.dp))
        }
    }
}

private fun AsteroidPhotoPanel(
@Composable
    state: AsteroidImageUiState,
    onRetry: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                AsteroidImageUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is AsteroidImageUiState.Available -> {
                    AsyncImage(
                        model = state.photo.imageUrl,
                        contentDescription = localizedString(
                            "Fotografía de NASA: ${state.photo.title}"
                        ),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                AsteroidImageUiState.Unavailable -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            "Fotografía real no disponible",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "NASA no tiene una imagen identificada para este asteroide.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                AsteroidImageUiState.Error -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            "No se pudo consultar la biblioteca de imágenes de NASA.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(onClick = onRetry) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Text(
                                "Reintentar",
                                modifier = Modifier.padding(start = 7.dp)
                            )
                        }
                    }
                }
            }
        }
        if (state is AsteroidImageUiState.Available) {
            Text(
                text = "NASA · ${state.photo.title} · ID ${state.photo.nasaId}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Text(
                "Solo se muestran fotografías reales verificadas; no se usan ilustraciones.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun MeasurementCard(
@Composable
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun MeasurementRow(
@Composable
    title: String,
    value: String,
    isLast: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 13.dp)
    ) {
        Text(
            title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            modifier = Modifier.padding(top = 3.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
        if (!isLast) {
            androidx.compose.material3.HorizontalDivider(
                modifier = Modifier.padding(top = 13.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f)
            )
        }
    }
}
