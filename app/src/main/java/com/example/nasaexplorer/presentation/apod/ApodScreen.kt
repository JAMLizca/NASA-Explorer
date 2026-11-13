package com.example.nasaexplorer.presentation.apod

import android.text.Html
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.nasaexplorer.data.local.AppDatabase
import com.example.nasaexplorer.data.local.entity.FavoriteEntity
import com.example.nasaexplorer.data.remote.apod.ApodDto
import com.example.nasaexplorer.data.remote.apod.RetrofitInstance
import com.example.nasaexplorer.data.repository.ApodRepository
import com.example.nasaexplorer.data.repository.FavoriteRepository
import kotlinx.coroutines.launch

@Composable
fun ApodScreen(
    onApodClick: (ApodDto) -> Unit = {}
) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val repository = remember {
        ApodRepository(RetrofitInstance.apodApi)
    }

    val factory = remember {
        ApodViewModelFactory(repository)
    }

    val apodViewModel: ApodViewModel = viewModel(
        factory = factory
    )

    val uiState by apodViewModel.uiState.collectAsState()

    // Conexión con Room
    val favoriteRepository = remember {
        FavoriteRepository(
            AppDatabase.getDatabase(context).favoriteDao()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Astronomy Picture of the Day",
            style = MaterialTheme.typography.headlineSmall
        )

        when (val state = uiState) {

            is ApodUiState.Loading -> {
                CircularProgressIndicator()
                Text("Conectando con NASA...")
            }

            is ApodUiState.Success -> {
                val apod = state.apod

                val favoriteId = "apod_${apod.date ?: "unknown"}"

                val isFavoriteFlow = remember(favoriteId) {
                    favoriteRepository.isFavorite(favoriteId)
                }

                val isFavorite by isFavoriteFlow.collectAsState(
                    initial = false
                )

                Text(
                    text = apod.title ?: "Sin título",
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = apod.date ?: "Fecha no disponible",
                    style = MaterialTheme.typography.bodyMedium
                )

                if (apod.mediaType == "image" && !apod.hdUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = apod.hdUrl,
                        contentDescription = apod.alt ?: apod.title,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        text = "El contenido de hoy no tiene una imagen disponible para mostrar."
                    )
                }

                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            if (isFavorite) {
                                favoriteRepository.deleteFavorite(favoriteId)
                            } else {
                                val cleanExplanation = Html.fromHtml(
                                    apod.explanation ?: "",
                                    Html.FROM_HTML_MODE_COMPACT
                                ).toString().trim()

                                favoriteRepository.insertFavorite(
                                    FavoriteEntity(
                                        id = favoriteId,
                                        title = apod.title ?: "APOD",
                                        description = cleanExplanation,
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
                        contentDescription = if (isFavorite) {
                            "Quitar de favoritos"
                        } else {
                            "Agregar a favoritos"
                        },
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = if (isFavorite) {
                        "Guardado en favoritos"
                    } else {
                        "Agregar a favoritos"
                    }
                )

                val cleanExplanation = remember(apod.explanation) {
                    Html.fromHtml(
                        apod.explanation ?: "No hay explicación disponible.",
                        Html.FROM_HTML_MODE_COMPACT
                    ).toString().trim()
                }

                Text(
                    text = cleanExplanation,
                    style = MaterialTheme.typography.bodyLarge
                )

                if (!apod.credit.isNullOrBlank()) {
                    Text(
                        text = "Crédito: ${apod.credit}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Button(
                    onClick = {
                        onApodClick(apod)
                    }
                ) {
                    Text("Ver detalles")
                }
            }

            is ApodUiState.Error -> {
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
                        text = "No pudimos cargar APOD",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { apodViewModel.loadApod() }
                    ) {
                        Text("Intentar nuevamente")
                    }
                }
            }
        }
    }
}
