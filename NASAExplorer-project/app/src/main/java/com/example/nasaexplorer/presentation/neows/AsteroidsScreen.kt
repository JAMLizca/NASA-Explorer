/** Compone la pantalla y conecta la interfaz con su estado. */
package com.example.nasaexplorer.presentation.neows

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import com.example.nasaexplorer.ui.components.Text
import androidx.compose.material3.TextButton as MaterialTextButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nasaexplorer.data.local.AppDatabase
import com.example.nasaexplorer.data.local.entity.FavoriteEntity
import com.example.nasaexplorer.data.remote.neows.AsteroidDto
import com.example.nasaexplorer.data.remote.neows.NeoWsRetrofitInstance
import com.example.nasaexplorer.data.repository.FavoriteRepository
import com.example.nasaexplorer.data.repository.NeoWsRepository
import com.example.nasaexplorer.domain.model.AsteroidSortOption
import com.example.nasaexplorer.ui.components.localizedString
import com.example.nasaexplorer.ui.theme.SpaceBlue
import com.example.nasaexplorer.ui.theme.SpaceCyanBright
import com.example.nasaexplorer.ui.theme.SpaceNavy
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/** Compone la interfaz asociada a esta sección de la pantalla. */
@Composable
fun AsteroidsScreen(
    onAsteroidClick: (AsteroidDto) -> Unit = {}
) {
    val context = LocalContext.current
    val repository = remember {
        NeoWsRepository(NeoWsRetrofitInstance.neoWsApi)
    }
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
    val startDate by asteroidViewModel.startDate.collectAsState()
    val endDate by asteroidViewModel.endDate.collectAsState()
    val statistics by asteroidViewModel.statistics.collectAsState()

    var sortMenuExpanded by remember {
        mutableStateOf(false)
    }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val filteredAsteroids by asteroidViewModel.filteredAsteroids.collectAsState(
        initial = emptyList()
    )

    when (val state = uiState) {
        AsteroidsUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(14.dp))
                    Text("Consultando objetos cercanos con NASA…")
                }
            }
        }

        is AsteroidsUiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (state.httpStatusCode == 429) {
                                MaterialTheme.colorScheme.tertiaryContainer
                            } else {
                                MaterialTheme.colorScheme.errorContainer
                            }
                        ) {
                            Icon(
                                if (state.httpStatusCode == 429) {
                                    Icons.Default.Warning
                                } else {
                                    Icons.Default.CloudOff
                                },
                                contentDescription = null,
                                tint = if (state.httpStatusCode == 429) {
                                    MaterialTheme.colorScheme.tertiary
                                } else {
                                    MaterialTheme.colorScheme.error
                                },
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        Text(
                            state.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            state.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = { asteroidViewModel.loadAsteroids() },
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Intentar nuevamente")
                        }
                    }
                }
            }
        }

        is AsteroidsUiState.Success -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 18.dp,
                    top = 14.dp,
                    end = 18.dp,
                    bottom = 112.dp
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    AsteroidHeader()
                }

                item {
                    Card(
                        shape = RoundedCornerShape(22.dp),
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
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    "Periodo de consulta",
                                    modifier = Modifier.padding(start = 8.dp),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                DateRangeButton(
                                    label = "DESDE",
                                    date = startDate,
                                    onClick = { showStartDatePicker = true },
                                    modifier = Modifier.weight(1f)
                                )
                                DateRangeButton(
                                    label = "HASTA",
                                    date = endDate,
                                    onClick = { showEndDatePicker = true },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Text(
                                "NeoWs admite periodos de hasta 7 días por consulta.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Button(
                                onClick = { asteroidViewModel.loadAsteroids() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Consultar periodo")
                            }
                        }
                    }

                    if (showStartDatePicker) {
                        NasaDatePickerDialog(
                            date = startDate,
                            onDismiss = { showStartDatePicker = false },
                            onDateSelected = {
                                asteroidViewModel.updateDateRange(it, endDate)
                                showStartDatePicker = false
                            }
                        )
                    }
                    if (showEndDatePicker) {
                        NasaDatePickerDialog(
                            date = endDate,
                            onDismiss = { showEndDatePicker = false },
                            onDateSelected = {
                                asteroidViewModel.updateDateRange(startDate, it)
                                showEndDatePicker = false
                            }
                        )
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                        SummaryMetric(
                            value = statistics.count.toString(),
                            label = "OBJETOS",
                            modifier = Modifier.weight(1f)
                        )
                        SummaryMetric(
                            value = statistics.hazardousCount.toString(),
                            label = "PELIGROSOS",
                            modifier = Modifier.weight(1f),
                            accent = true
                        )
                        SummaryMetric(
                            value = statistics.largestDiameterMeters
                                ?.let { "%.1f m".format(Locale.US, it) } ?: "—",
                            label = "MÁX. DIÁMETRO",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = asteroidViewModel::updateSearchQuery,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null)
                        },
                        label = { Text("Buscar por nombre") },
                        placeholder = { Text("Ej. Apophis") },
                        singleLine = true
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = onlyHazardous,
                            onClick = {
                                asteroidViewModel.updateHazardousFilter(!onlyHazardous)
                            },
                            label = { Text("Potencialmente peligrosos") },
                            leadingIcon = {
                                Icon(
                                    if (onlyHazardous) Icons.Default.Warning else Icons.Default.FilterAlt,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )

                        Box {
                            OutlinedButton(
                                onClick = { sortMenuExpanded = true },
                                shape = RoundedCornerShape(14.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 9.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Sort,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    when (sortOption) {
                                        AsteroidSortOption.NAME -> "Nombre"
                                        AsteroidSortOption.DIAMETER -> "Diámetro"
                                        AsteroidSortOption.SPEED -> "Velocidad"
                                        AsteroidSortOption.DISTANCE -> "Distancia"
                                    },
                                    modifier = Modifier.padding(start = 5.dp),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                            DropdownMenu(
                                expanded = sortMenuExpanded,
                                onDismissRequest = { sortMenuExpanded = false }
                            ) {
                                listOf(
                                    AsteroidSortOption.NAME to "Nombre",
                                    AsteroidSortOption.DIAMETER to "Mayor diámetro",
                                    AsteroidSortOption.SPEED to "Mayor velocidad",
                                    AsteroidSortOption.DISTANCE to "Menor distancia"
                                ).forEach { (option, label) ->
                                    DropdownMenuItem(
                                        text = { Text(label) },
                                        onClick = {
                                            asteroidViewModel.updateSortOption(option)
                                            sortMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        "${filteredAsteroids.size} objetos encontrados",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (filteredAsteroids.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.Public,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(8.dp))
                                Text("No hay asteroides para estos filtros.")
                                Text(
                                    "Prueba con otro nombre o desactiva el filtro.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                } else {
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
    }
}

private fun AsteroidHeader() {
@Composable
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    listOf(SpaceNavy, Color(0xFF0B2C58), Color(0xFF103A68))
                )
            )
            .padding(horizontal = 20.dp, vertical = 19.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(13.dp),
                    color = SpaceCyanBright.copy(alpha = 0.12f)
                ) {
                    Icon(
                        Icons.Default.Public,
                        contentDescription = null,
                        tint = SpaceCyanBright,
                        modifier = Modifier.padding(11.dp).size(23.dp)
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    Text(
                        "NASA · NEOWS",
                        style = MaterialTheme.typography.labelSmall,
                        color = SpaceCyanBright,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.1.sp
                    )
                    Text(
                        "Objetos cercanos a la Tierra",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 28.sp
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.16f))
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "Aproximaciones y datos orbitales publicados por NASA.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.82f)
            )
        }
    }
}

private fun DateRangeButton(
@Composable
    label: String,
    date: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                date,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}

private fun SummaryMetric(
@Composable
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    accent: Boolean = false
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (accent) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
        )
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 13.dp)
        ) {
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (accent) {
                    MaterialTheme.colorScheme.onSecondaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                maxLines = 1
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = if (accent) {
                    MaterialTheme.colorScheme.onSecondaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                maxLines = 1
            )
        }
    }
}

private fun MetricValue(
@Composable
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(top = 5.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(11.dp)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(3.dp))
        Text(value, style = MaterialTheme.typography.bodySmall)
    }
}

private fun AsteroidCard(
@Composable
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
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(15.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.padding(end = 12.dp)
                ) {
                    Icon(
                        Icons.Default.Public,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Column(
                    Modifier
                        .weight(1f)
                        .padding(vertical = 3.dp)
                ) {
                    Text(
                        text = "NASA ID ${asteroid.id ?: "No disponible"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = asteroid.name ?: "Asteroide sin nombre",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2
                    )
                }

                AsteroidFavoriteButton(
                    asteroid = asteroid,
                    favoriteRepository = favoriteRepository
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricValue(
                    label = "DIÁMETRO",
                    value = if (minimumDiameter != null && maximumDiameter != null) {
                        "${"%.2f".format(Locale.US, minimumDiameter)}–${"%.2f".format(Locale.US, maximumDiameter)} m"
                    } else "No disponible",
                    modifier = Modifier.weight(1f)
                )
                MetricValue(
                    label = "VELOCIDAD",
                    value = approach?.relativeVelocity?.kilometersPerHour?.let { "$it km/h" }
                        ?: "No disponible",
                    modifier = Modifier.weight(1f)
                )
            }

            MetricValue(
                label = "DISTANCIA DE APROXIMACIÓN · ${approach?.date ?: "FECHA NO DISPONIBLE"}",
                value = approach?.missDistance?.kilometers?.let { "$it km" } ?: "No disponible",
                modifier = Modifier.fillMaxWidth()
            )

            val classificationColor = when (asteroid.isPotentiallyHazardous) {
                true -> MaterialTheme.colorScheme.error
                false -> MaterialTheme.colorScheme.primary
                null -> MaterialTheme.colorScheme.onSurfaceVariant
            }
            val classificationText = when (asteroid.isPotentiallyHazardous) {
                true -> "Potencialmente peligroso"
                false -> "No potencialmente peligroso"
                null -> "Clasificación no disponible"
            }
            Surface(
                shape = RoundedCornerShape(50.dp),
                color = classificationColor.copy(alpha = 0.12f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    if (asteroid.isPotentiallyHazardous == true) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = classificationColor,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Text(
                        text = "NASA · $classificationText",
                        color = classificationColor,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

    }
}

/** Compone la interfaz asociada a esta sección de la pantalla. */
@Composable
fun AsteroidFavoriteButton(
    asteroid: AsteroidDto,
    favoriteRepository: FavoriteRepository
) {
    val nasaId = asteroid.id ?: return
    val asteroidId = "neo_$nasaId"

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
                                asteroid.estimatedDiameter?.meters?.minimum
                                    ?.let { "%.2f".format(Locale.US, it) } ?: "No disponible"
                            } metros.
                            Diámetro máximo: ${
                                asteroid.estimatedDiameter?.meters?.maximum
                                    ?.let { "%.2f".format(Locale.US, it) } ?: "No disponible"
                            } metros.
                            Velocidad: ${
                                approach?.relativeVelocity?.kilometersPerHour ?: "No disponible"
                            } km/h.
                            Distancia: ${
                                approach?.missDistance?.kilometers ?: "No disponible"
                            } km.
                            Clasificación NASA: ${when (asteroid.isPotentiallyHazardous) {
                                true -> "Potencialmente peligroso"
                                false -> "No potencialmente peligroso"
                                null -> "No disponible"
                            }}.
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
            contentDescription = localizedString(
                if (isFavorite) "Quitar de favoritos" else "Agregar a favoritos"
            ),
            tint = if (isFavorite) {
                MaterialTheme.colorScheme.tertiary
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

private fun NasaDatePickerDialog(
@Composable
    date: String,
    onDismiss: () -> Unit,
    onDateSelected: (String) -> Unit
) {
    val initialDateMillis = remember(date) {
        runCatching {
            SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
                isLenient = false
                timeZone = TimeZone.getTimeZone("UTC")
            }.parse(date)?.time
        }.getOrNull()
    }
    val datePickerState = androidx.compose.material3.rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            MaterialTextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
                            timeZone = TimeZone.getTimeZone("UTC")
                        }
                        onDateSelected(formatter.format(Date(millis)))
                    }
                },
                enabled = datePickerState.selectedDateMillis != null
            ) {
                Text("Seleccionar")
            }
        },
        dismissButton = {
            MaterialTextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        DatePicker(state = datePickerState)
    }
}
