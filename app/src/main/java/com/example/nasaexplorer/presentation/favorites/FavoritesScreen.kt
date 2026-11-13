package com.example.nasaexplorer.presentation.favorites

import android.text.Html
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nasaexplorer.data.local.AppDatabase
import com.example.nasaexplorer.data.repository.FavoriteRepository

@Composable
fun FavoritesScreen() {

    val context = LocalContext.current

    val favoriteDao = remember {
        AppDatabase.getDatabase(context).favoriteDao()
    }

    val repository = remember {
        FavoriteRepository(favoriteDao)
    }

    val factory = remember {
        FavoritesViewModelFactory(repository)
    }

    val favoritesViewModel: FavoritesViewModel = viewModel(
        factory = factory
    )

    val favorites by favoritesViewModel.favorites.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Mis favoritos",
                style = MaterialTheme.typography.headlineMedium
            )

            if (favorites.isNotEmpty()) {
                TextButton(
                    onClick = {
                        favoritesViewModel.removeAllFavorites()
                    }
                ) {
                    Text("Eliminar todos")
                }
            }
        }

        if (favorites.isEmpty()) {

            Spacer(modifier = Modifier.padding(12.dp))

            Text(
                text = "Todavía no tienes favoritos guardados.",
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "Explora APOD y Asteroides para guardar tus elementos preferidos.",
                style = MaterialTheme.typography.bodyMedium
            )

        } else {

            Text(
                text = "Elementos guardados: ${favorites.size}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.padding(top = 8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = favorites,
                    key = { it.id }
                ) { favorite ->

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = when (favorite.type) {
                                    "APOD" -> "Imagen astronómica del día"
                                    "ASTEROID" -> "Asteroide cercano"
                                    else -> "Elemento guardado"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.padding(top = 4.dp))

                            Text(
                                text = favorite.title,
                                style = MaterialTheme.typography.titleMedium
                            )

                            if (favorite.date.isNotBlank()) {
                                Text(
                                    text = "Fecha: ${favorite.date}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            if (favorite.description.isNotBlank()) {
                                val cleanDesc = remember(favorite.description) {
                                    Html.fromHtml(
                                        favorite.description,
                                        Html.FROM_HTML_MODE_COMPACT
                                    ).toString().trim()
                                }
                                Text(
                                    text = cleanDesc,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }

                            TextButton(
                                onClick = {
                                    favoritesViewModel.removeFavorite(favorite.id)
                                },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Eliminar de favoritos")
                            }
                        }
                    }
                }
            }
        }
    }
}
