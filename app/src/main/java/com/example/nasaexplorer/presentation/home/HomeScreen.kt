/** Construye la pantalla principal y conecta la interfaz con su estado. */
package com.example.nasaexplorer.presentation.home

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import com.example.nasaexplorer.ui.components.Text
import coil.compose.AsyncImage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.nasaexplorer.BuildConfig
import com.example.nasaexplorer.R
import com.example.nasaexplorer.data.remote.donki.DonkiNotificationDto
import com.example.nasaexplorer.data.remote.donki.DonkiMessageBlock
import com.example.nasaexplorer.data.remote.donki.parseDonkiMessageBody
import com.example.nasaexplorer.data.remote.donki.summarizeDonkiMessage
import com.example.nasaexplorer.data.remote.donki.DonkiRetrofitInstance
import com.example.nasaexplorer.data.remote.eonet.EonetEventDto
import com.example.nasaexplorer.data.remote.eonet.EonetRetrofitInstance
import com.example.nasaexplorer.data.remote.eonet.satelliteImageUrl
import com.example.nasaexplorer.data.repository.DonkiRepository
import com.example.nasaexplorer.data.repository.EonetRepository
import com.example.nasaexplorer.ui.components.localizedString
import com.google.android.gms.tasks.Task
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Composable
/** Dibuja en Compose la interfaz asociada a home screen */
fun HomeScreen(
    onApodClick: () -> Unit,
    onAsteroidsClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onEonetMapClick: () -> Unit
) {
    val heroPhrases = listOf(
        "El universo, más cerca.",
        "Explora el cosmos con datos de NASA.",
        "Descubre imágenes que cuentan historias.",
        "Observa la Tierra desde otra perspectiva.",
        "Cada dato abre una ventana al espacio."
    )
    var heroPhraseIndex by rememberSaveable { mutableStateOf(0) }
    // Rota frases locales sin generar solicitudes de red.
    LaunchedEffect(heroPhrases.size) {
        while (true) {
            delay(5_000)
            heroPhraseIndex = (heroPhraseIndex + 1) % heroPhrases.size
        }
    }

    val donkiRepository = androidx.compose.runtime.remember {
        DonkiRepository(DonkiRetrofitInstance.api)
    }
    val eonetRepository = androidx.compose.runtime.remember {
        EonetRepository(EonetRetrofitInstance.api)
    }
    val eventsViewModel: HomeEventsViewModel = viewModel(
        factory = androidx.compose.runtime.remember(donkiRepository, eonetRepository) {
            HomeEventsViewModelFactory(
                donkiRepository = donkiRepository,
                eonetRepository = eonetRepository,
                nasaApiKey = BuildConfig.NASA_API_KEY
            )
        }
    )
    val donkiState by eventsViewModel.donkiState.collectAsState()
    val eonetState by eventsViewModel.eonetState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.mipmap.nasa_explorer_launcher),
                contentDescription = localizedString("Logotipo de NASA Explorer"),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    "NASA EXPLORER",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "EXPLORACIÓN ASTRONÓMICA",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    "DATOS NASA",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "NASA · EXPLORACIÓN",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Crossfade(
                    targetState = heroPhrases[heroPhraseIndex],
                    label = "home-hero-phrase"
                ) { phrase ->
                    Text(
                        phrase,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Spacer(Modifier.height(26.dp))

        Text(
            "Explorar",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))

        ModuleCard(
            icon = Icons.Default.WbSunny,
            eyebrow = "APOD · ASTRONOMY PICTURE OF THE DAY",
            title = "Imagen astronómica del día",
            subtitle = "La selección diaria de NASA.",
            onClick = onApodClick
        )
        Spacer(Modifier.height(10.dp))
        ModuleCard(
            icon = Icons.Default.Public,
            eyebrow = "NEOWS · OBJETOS CERCANOS A LA TIERRA",
            title = "Asteroides cercanos",
            subtitle = "Seguimiento de objetos cercanos.",
            onClick = onAsteroidsClick
        )

        Spacer(Modifier.height(24.dp))
        SpaceActivitySection(
            donkiState = donkiState,
            eonetState = eonetState,
            onRetryDonki = eventsViewModel::loadDonki,
            onRetryEonet = eventsViewModel::loadEonet,
            onEonetMapClick = onEonetMapClick
        )

        Spacer(Modifier.height(26.dp))

        Text(
            "Tu colección",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Contenido que guardaste para volver a consultar.",
            modifier = Modifier.padding(top = 4.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(14.dp))
        ModuleCard(
            icon = Icons.Default.Favorite,
            eyebrow = "ELEMENTOS GUARDADOS",
            title = "Favoritos",
            subtitle = "Tus imágenes y objetos astronómicos guardados.",
            onClick = onFavoritesClick
        )

        Spacer(Modifier.height(18.dp))
        Text(
            "Proyecto independiente. Los datos astronómicos pertenecen a sus respectivas fuentes.",
            modifier = Modifier.padding(horizontal = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(112.dp))
    }
}

@Composable
private fun SpaceActivitySection(
    donkiState: FeedUiState<DonkiNotificationDto>,
    eonetState: FeedUiState<EonetEventDto>,
    onRetryDonki: () -> Unit,
    onRetryEonet: () -> Unit,
    onEonetMapClick: () -> Unit
) {
    var donkiExpanded by rememberSaveable { mutableStateOf(false) }
    var selectedDonkiNotification by remember { mutableStateOf<DonkiNotificationDto?>(null) }
    var selectedEonetEvent by remember { mutableStateOf<EonetEventDto?>(null) }
    Column(verticalArrangement = Arrangement.spacedBy(11.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "NASA · ACTIVIDAD ESPACIAL",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = MaterialTheme.typography.labelSmall.letterSpacing
                )
                Text(
                    "Actividad del Sol y la Tierra",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Public,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(10.dp).size(20.dp)
                )
            }
        }

        SpaceFeed(
            eyebrow = "NASA DONKI · CLIMA ESPACIAL",
            icon = Icons.Default.WbSunny,
            state = donkiState,
            loadingText = "Cargando notificaciones de clima espacial…",
            emptyText = "NASA no publicó notificaciones recientes de clima espacial.",
            onRetry = onRetryDonki,
            visibleItemCount = if (donkiExpanded) Int.MAX_VALUE else DONKI_COLLAPSED_ITEM_COUNT,
            expanded = donkiExpanded,
            onToggleExpanded = { donkiExpanded = !donkiExpanded }
        ) { notification ->
            Card(
                onClick = { selectedDonkiNotification = notification },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        listOf(
                            localizedDonkiType(notification.messageType),
                            notification.messageIssueTime.take(10)
                        ).filter(String::isNotBlank).joinToString(" · "),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (notification.messageBody.isNotBlank()) {
                        Text(
                            parseDonkiMessageBody(notification.messageBody)
                                .take(2)
                                .joinToString(" ") { it.text },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        SpaceFeed(
            eyebrow = "NASA EONET · EVENTOS ACTIVOS",
            icon = Icons.Default.Public,
            state = eonetState,
            loadingText = "Cargando eventos naturales activos…",
            emptyText = "EONET no tiene eventos activos para mostrar.",
            onRetry = onRetryEonet,
            horizontalCarousel = true,
            headerAction = onEonetMapClick
        ) { event ->
            val latestGeometry = event.geometry.maxByOrNull { it.date }
            val imageUrl = event.satelliteImageUrl()
            Card(
                onClick = { selectedEonetEvent = event },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f)
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(206.dp)
                        .clip(RoundedCornerShape(18.dp))
                ) {
                    if (imageUrl != null) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = localizedString("Vista satelital del área del evento"),
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {}
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        androidx.compose.ui.graphics.Color.Transparent,
                                        androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.78f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .padding(15.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val category = event.categories.firstOrNull()
                        category?.let {
                            Text(
                                "NASA GIBS · ${localizedEonetCategory(it.id, it.title).uppercase()}",
                                style = MaterialTheme.typography.labelSmall,
                                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.85f),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            event.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = androidx.compose.ui.graphics.Color.White,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        latestGeometry?.let { geometry ->
                            val details = listOfNotNull(
                                geometry.date.take(10).takeIf(String::isNotBlank),
                                geometry.magnitudeValue?.let { value ->
                                    "${formatMagnitude(value)} ${geometry.magnitudeUnit.orEmpty()}".trim()
                                }
                            ).joinToString(" · ")
                            if (details.isNotBlank()) {
                                Text(
                                    details,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.82f)
                                )
                            }
                        }
                    }
                }
            }
        }

        Text(
            "Datos publicados por NASA DONKI y NASA EONET; las fuentes pueden actualizarse.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    selectedDonkiNotification?.let { notification ->
        DonkiNotificationDialog(
            notification = notification,
            onDismiss = { selectedDonkiNotification = null }
        )
    }

    selectedEonetEvent?.let { event ->
        val latestGeometry = event.geometry.maxByOrNull { it.date }
        val imageUrl = event.satelliteImageUrl()
        var imageLoadFailed by remember(event.id) { mutableStateOf(false) }
        Dialog(
            onDismissRequest = { selectedEonetEvent = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.94f)
                    .heightIn(max = (LocalConfiguration.current.screenHeightDp * 0.88f).dp),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 24.dp
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    ) {
                        // GIBS muestra la zona del evento, no necesariamente el fenómeno de cerca.
                        if (imageUrl == null || imageLoadFailed) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(
                                                MaterialTheme.colorScheme.primaryContainer,
                                                MaterialTheme.colorScheme.surfaceVariant
                                            )
                                        )
                                    ),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.Public,
                                    contentDescription = null,
                                    modifier = Modifier.size(44.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.height(9.dp))
                                Text(
                                    "Imagen satelital no disponible",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        if (imageUrl != null && !imageLoadFailed) {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = localizedString("Vista satelital del área del evento"),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
                                contentScale = ContentScale.Crop,
                                onError = { imageLoadFailed = true }
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.18f),
                                            androidx.compose.ui.graphics.Color.Transparent,
                                            androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.78f)
                                        )
                                    )
                                )
                        )
                        IconButton(
                            onClick = { selectedEonetEvent = null },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                                .background(
                                    androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.42f),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = localizedString("Cerrar"),
                                tint = androidx.compose.ui.graphics.Color.White
                            )
                        }
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 18.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.42f)
                            ) {
                                Text(
                                    "NASA EONET · ${event.categories.firstOrNull()?.let {
                                        localizedEonetCategory(it.id, it.title).uppercase()
                                    } ?: localizedString("EVENTO NATURAL")}",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = androidx.compose.ui.graphics.Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                event.title,
                                style = MaterialTheme.typography.headlineSmall,
                                color = androidx.compose.ui.graphics.Color.White,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .heightIn(max = 390.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp, vertical = 18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "INFORMACIÓN DEL EVENTO",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Vista satelital de la zona asociada al evento. La imagen muestra el área y no necesariamente el fenómeno de cerca.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        latestGeometry?.let { geometry ->
                            if (geometry.date.isNotBlank()) {
                                EventDetailRow(label = "Fecha", value = geometry.date)
                            }
                            geometry.magnitudeValue?.let { magnitude ->
                                val formatted =
                                    "${formatMagnitude(magnitude)} ${geometry.magnitudeUnit.orEmpty()}".trim()
                                EventDetailRow(label = "Magnitud", value = formatted)
                            }
                        }
                        if (event.id.isNotBlank()) {
                            EventDetailRow(label = "Identificador EONET", value = event.id)
                        }
                    }
                    TextButton(
                        onClick = { selectedEonetEvent = null },
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }
}

@Composable
private fun DonkiNotificationDialog(
    notification: DonkiNotificationDto,
    onDismiss: () -> Unit
) {
    val allBlocks = remember(notification.messageBody) {
        parseDonkiMessageBody(notification.messageBody)
    }
    val summaryBlocks = remember(allBlocks) { summarizeDonkiMessage(allBlocks) }
    val translator = remember(notification) {
        Translation.getClient(
            TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.SPANISH)
                .build()
        )
    }
    val scope = rememberCoroutineScope()
    val translationFailedMessage = localizedString("No se pudo completar la traducción.")
    var showFullReport by remember(notification) { mutableStateOf(false) }
    var translatedBlocks by remember(notification) {
        mutableStateOf<List<DonkiMessageBlock>?>(null)
    }
    var translationError by remember(notification) { mutableStateOf<String?>(null) }
    var isTranslating by remember(notification) { mutableStateOf(false) }

    DisposableEffect(translator) {
        onDispose { translator.close() }
    }

    val sourceBlocks = if (showFullReport) allBlocks else summaryBlocks
    val contentBlocks = translatedBlocks ?: sourceBlocks

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        Icons.Default.WbSunny,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(10.dp).size(22.dp)
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        localizedDonkiType(notification.messageType),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "NASA DONKI · ${notification.messageType}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (notification.messageIssueTime.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            notification.messageIssueTime,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                if (allBlocks.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                showFullReport = !showFullReport
                                translatedBlocks = null
                                translationError = null
                            },
                            enabled = !isTranslating
                        ) {
                            Text(if (showFullReport) "Mostrar resumen" else "Ver informe completo")
                        }
                        TextButton(
                            onClick = {
                                if (translatedBlocks != null) {
                                    translatedBlocks = null
                                    translationError = null
                                } else {
                                    scope.launch {
                                        isTranslating = true
                                        translationError = null
                                        try {
                                            translator.downloadModelIfNeeded(
                                                DownloadConditions.Builder().build()
                                            ).awaitResult()
                                            translatedBlocks = sourceBlocks.map { block ->
                                                block.copy(text = translator.translate(block.text).awaitResult())
                                            }
                                        } catch (e: CancellationException) {
                                            throw e
                                        } catch (e: Exception) {
                                            translationError = e.localizedMessage
                                                ?.takeIf(String::isNotBlank)
                                                ?: translationFailedMessage
                                        } finally {
                                            isTranslating = false
                                        }
                                    }
                                }
                            },
                            enabled = !isTranslating
                        ) {
                            if (isTranslating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(if (translatedBlocks == null) "Traducir al español" else "Ver original")
                            }
                        }
                    }
                    if (translatedBlocks == null && !isTranslating) {
                        Text(
                            "La primera traducción descarga un modelo de idioma (aprox. 30 MB).",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    translationError?.let { message ->
                        Text(
                            localizedString("No se pudo completar la traducción.") + " $message",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                if (contentBlocks.isEmpty()) {
                    Text(
                        localizedString("Notificación sin descripción."),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    contentBlocks.forEach { block ->
                        Text(
                            block.text,
                            style = if (block.isHeading) {
                                MaterialTheme.typography.titleSmall
                            } else {
                                MaterialTheme.typography.bodyMedium
                            },
                            fontWeight = if (block.isHeading) FontWeight.Bold else FontWeight.Normal,
                            color = if (block.isHeading) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

private suspend fun <T> Task<T>.awaitResult(): T =
    suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { result ->
            if (continuation.isActive) continuation.resume(result)
        }
        addOnFailureListener { error ->
            if (continuation.isActive) continuation.resumeWithException(error)
        }
    }

@Composable
private fun EventDetailRow(label: String, value: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            Text(value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

private fun formatMagnitude(value: Double): String =
    if (value % 1.0 == 0.0) value.toLong().toString() else String.format(Locale.US, "%.1f", value)

@Composable
private fun <T> SpaceFeed(
    eyebrow: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    state: FeedUiState<T>,
    loadingText: String,
    emptyText: String,
    onRetry: () -> Unit,
    visibleItemCount: Int? = null,
    expanded: Boolean = false,
    onToggleExpanded: (() -> Unit)? = null,
    horizontalCarousel: Boolean = false,
    headerAction: (() -> Unit)? = null,
    content: @Composable (T) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    eyebrow,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            headerAction?.let { onAction ->
                TextButton(onClick = onAction) {
                    Icon(Icons.Default.Map, contentDescription = null)
                    Text(
                        localizedString("Mapa"),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
        when (state) {
            FeedUiState.Loading -> {
                FeedMessageCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                        Text(
                            loadingText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            is FeedUiState.Error -> {
                FeedMessageCard {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            state.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(onClick = onRetry) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Text("Reintentar", modifier = Modifier.padding(start = 7.dp))
                        }
                    }
                }
            }
            is FeedUiState.Success -> {
                if (state.items.isEmpty()) {
                    FeedMessageCard {
                        Text(
                            emptyText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    if (horizontalCarousel) {
                        val pagerState = rememberPagerState(pageCount = { state.items.size })
                        HorizontalPager(
                            state = pagerState,
                            pageSpacing = 12.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) { page ->
                            content(state.items[page])
                        }
                        if (state.items.size > 1) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 3.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                repeat(state.items.size) { page ->
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 3.dp)
                                            .width(if (page == pagerState.currentPage) 18.dp else 6.dp)
                                            .height(6.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (page == pagerState.currentPage) {
                                                    MaterialTheme.colorScheme.primary
                                                } else {
                                                    MaterialTheme.colorScheme.outlineVariant
                                                }
                                            )
                                    )
                                }
                                Text(
                                    "${pagerState.currentPage + 1} / ${state.items.size}",
                                    modifier = Modifier.padding(start = 8.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        val visibleItems = visibleItemCount?.let(state.items::take) ?: state.items
                        visibleItems.forEach { item -> content(item) }
                        if (visibleItemCount != null && onToggleExpanded != null &&
                            (state.items.size > visibleItems.size ||
                                (expanded && state.items.size > DONKI_COLLAPSED_ITEM_COUNT))
                        ) {
                            TextButton(
                                onClick = onToggleExpanded,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Icon(
                                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null
                                )
                                Text(
                                    if (expanded) "Ver menos" else "Ver más",
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private const val DONKI_COLLAPSED_ITEM_COUNT = 1

@Composable
private fun FeedMessageCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
private fun localizedDonkiType(type: String): String = when (type) {
    "CME" -> localizedString("Eyección de masa coronal")
    "FLR" -> localizedString("Llamarada solar")
    "GST" -> localizedString("Tormenta geomagnética")
    "IPS" -> localizedString("Choque interplanetario")
    "SEP" -> localizedString("Partículas energéticas solares")
    "RBE" -> localizedString("Evento de radiación")
    "HSS" -> localizedString("Corriente solar de alta velocidad")
    "MPC" -> localizedString("Conjunción planetaria")
    else -> type.ifBlank { localizedString("Notificación de clima espacial") }
}

@Composable
private fun localizedEonetCategory(id: String, title: String): String = when (id) {
    "wildfires" -> localizedString("Incendios forestales")
    "severeStorms" -> localizedString("Tormentas severas")
    "volcanoes" -> localizedString("Volcanes")
    "seaLakeIce" -> localizedString("Hielo marino y lacustre")
    "floods" -> localizedString("Inundaciones")
    "earthquakes" -> localizedString("Terremotos")
    "landslides" -> localizedString("Deslizamientos de tierra")
    "drought" -> localizedString("Sequía")
    "dustHaze" -> localizedString("Polvo y neblina")
    "snow" -> localizedString("Nieve")
    else -> title.ifBlank { localizedString("Evento natural") }
}

@Composable
private fun ModuleCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    eyebrow: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(13.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 13.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    eyebrow,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = localizedString("Abrir $title"),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
