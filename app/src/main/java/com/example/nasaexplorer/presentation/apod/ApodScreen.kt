/** Construye la pantalla principal y conecta la interfaz con su estado. */
package com.example.nasaexplorer.presentation.apod

import android.text.Html
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import com.example.nasaexplorer.ui.components.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.nasaexplorer.data.local.AppDatabase
import com.example.nasaexplorer.data.local.entity.FavoriteEntity
import com.example.nasaexplorer.data.remote.apod.ApodDto
import com.example.nasaexplorer.data.remote.apod.RetrofitInstance
import com.example.nasaexplorer.data.repository.ApodRepository
import com.example.nasaexplorer.data.repository.FavoriteRepository
import com.example.nasaexplorer.ui.components.localizedString
import com.example.nasaexplorer.ui.theme.SpaceBlue
import com.example.nasaexplorer.ui.theme.SpaceCyan
import com.example.nasaexplorer.ui.theme.SpaceCyanBright
import com.example.nasaexplorer.ui.theme.SpaceNavy
import kotlinx.coroutines.launch

@Composable
/** Dibuja en Compose la interfaz asociada a apod screen */
fun ApodScreen(
    onApodClick: (ApodDto) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { ApodRepository(RetrofitInstance.apodApi) }
    val factory = remember { ApodViewModelFactory(repository) }
    val apodViewModel: ApodViewModel = viewModel(factory = factory)
    val uiState by apodViewModel.uiState.collectAsState()
    val favoriteRepository = remember {
        FavoriteRepository(AppDatabase.getDatabase(context).favoriteDao())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    Icons.Default.WbSunny,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(11.dp)
                )
            }
            Column(Modifier.padding(start = 12.dp)) {
                Text(
                    "EL UNIVERSO DE HOY",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text("Astronomy Picture of the Day", style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(Modifier.height(18.dp))

        when (val state = uiState) {
            ApodUiState.Loading -> LoadingApod()
            is ApodUiState.Error -> ErrorApod(
                message = state.message,
                onRetry = apodViewModel::loadApod
            )
            is ApodUiState.Success -> {
                val apod = state.apod
                val favoriteId = "apod_${apod.date ?: "unknown"}"
                val isFavoriteFlow = remember(favoriteId) {
                    favoriteRepository.isFavorite(favoriteId)
                }
                val isFavorite by isFavoriteFlow.collectAsState(initial = false)
                val explanation = remember(apod.explanation) {
                    Html.fromHtml(
                        apod.explanation ?: "No hay explicación disponible.",
                        Html.FROM_HTML_MODE_COMPACT
                    ).toString().trim()
                }
                val credit = remember(apod.credit, apod.copyright) {
                    Html.fromHtml(
                        apod.credit ?: apod.copyright.orEmpty(),
                        Html.FROM_HTML_MODE_COMPACT
                    ).toString().trim()
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column {
                        Box {
                            if (apod.mediaType == "image" && !apod.hdUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = apod.hdUrl,
                                    contentDescription = apod.alt ?: apod.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(270.dp)
                                        .clip(RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp))
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(220.dp)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(
                                                    SpaceNavy,
                                                    SpaceBlue,
                                                    SpaceCyan
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            Icons.Default.OpenInFull,
                                            contentDescription = null,
                                            tint = SpaceCyanBright
                                        )
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            "Contenido multimedia",
                                            color = Color.White,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            apod.mediaType ?: "Tipo no especificado",
                                            color = Color(0xFFD8E8F7),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(14.dp),
                                shape = RoundedCornerShape(50),
                                color = Color.Black.copy(alpha = 0.58f)
                            ) {
                                Text(
                                    "NASA · APOD",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(10.dp),
                                shape = CircleShape,
                                color = Color.Black.copy(alpha = 0.58f)
                            ) {
                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            if (isFavorite) {
                                                favoriteRepository.deleteFavorite(favoriteId)
                                            } else {
                                                favoriteRepository.insertFavorite(
                                                    FavoriteEntity(
                                                        id = favoriteId,
                                                        title = apod.title ?: "APOD",
                                                        description = explanation,
                                                        imageUrl = apod.hdUrl ?: apod.url ?: "",
                                                        date = apod.date ?: "",
                                                        type = "APOD"
                                                    )
                                                )
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
                                        contentDescription = localizedString(
                                            if (isFavorite) "Quitar de favoritos"
                                            else "Agregar a favoritos"
                                        ),
                                        tint = if (isFavorite) {
                                            MaterialTheme.colorScheme.tertiary
                                        } else {
                                            Color.White
                                        }
                                    )
                                }
                            }
                        }

                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                apod.date ?: "Fecha no disponible",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                apod.title ?: "Sin título",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            if (credit.isNotBlank()) {
                                Text(
                                    "Imagen: $credit",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                explanation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 5
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { onApodClick(apod) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Text("Explorar historia")
                                }
                                OutlinedButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            if (isFavorite) {
                                                favoriteRepository.deleteFavorite(favoriteId)
                                            } else {
                                                favoriteRepository.insertFavorite(
                                                    FavoriteEntity(
                                                        id = favoriteId,
                                                        title = apod.title ?: "APOD",
                                                        description = explanation,
                                                        imageUrl = apod.hdUrl ?: apod.url ?: "",
                                                        date = apod.date ?: "",
                                                        type = "APOD"
                                                    )
                                                )
                                            }
                                        }
                                    },
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Text(if (isFavorite) "Guardado" else "Guardar")
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    "Imagen astronómica seleccionada por NASA para esta fecha.",
                    modifier = Modifier.padding(horizontal = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(Modifier.height(96.dp))
    }
}

@Composable
private fun LoadingApod() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 56.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CircularProgressIndicator()
            Text("Conectando con NASA…", style = MaterialTheme.typography.titleMedium)
            Text(
                "Preparando el descubrimiento de hoy",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorApod(message: String, onRetry: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.CloudOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
            Text("No pudimos cargar APOD", style = MaterialTheme.typography.titleLarge)
            Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors()
            ) {
                Text("Intentar nuevamente")
            }
        }
    }
}
