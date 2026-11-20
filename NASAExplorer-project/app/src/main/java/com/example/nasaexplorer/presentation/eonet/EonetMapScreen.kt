/** Presenta los eventos naturales activos sobre un mapa interactivo de OpenStreetMap. */
package com.example.nasaexplorer.presentation.eonet

import android.graphics.Color as AndroidColor
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nasaexplorer.data.remote.eonet.EonetRetrofitInstance
import com.example.nasaexplorer.data.repository.EonetRepository
import com.example.nasaexplorer.presentation.home.FeedUiState
import com.example.nasaexplorer.ui.components.Text
import com.example.nasaexplorer.ui.components.localizedString
import com.google.gson.Gson

/** Carga eventos activos, muestra sus ubicaciones y permite consultar sus detalles. */
@Composable
fun EonetMapScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val repository = remember { EonetRepository(EonetRetrofitInstance.api) }
    val mapViewModel: EonetMapViewModel = viewModel(
        factory = remember(repository) { EonetMapViewModelFactory(repository) }
    )
    val state by mapViewModel.state.collectAsState()
    var selectedEventId by rememberSaveable { mutableStateOf<String?>(null) }
    var mapLoadFailed by remember { mutableStateOf(false) }
    var mapInstance by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = localizedString("Volver")
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    localizedString("Mapa de eventos EONET"),
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    localizedString("Eventos naturales activos"),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.Public,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        when (val currentState = state) {
            FeedUiState.Loading -> {
                MapStatus(
                    modifier = Modifier.weight(1f),
                    message = localizedString("Cargando eventos naturales…"),
                    loading = true
                )
            }

            is FeedUiState.Error -> {
                MapStatus(
                    modifier = Modifier.weight(1f),
                    message = currentState.message,
                    onRetry = mapViewModel::loadEvents
                )
            }

            is FeedUiState.Success -> {
                val points = currentState.items
                val selectedPoint = points.firstOrNull { it.id == selectedEventId }
                val markersJson = remember(points) {
                    Gson().toJson(
                        points.map { point ->
                            mapOf(
                                "id" to point.id,
                                "title" to point.title,
                                "category" to point.category,
                                "latitude" to point.latitude,
                                "longitude" to point.longitude
                            )
                        }
                    )
                }

                if (points.isEmpty()) {
                    MapStatus(
                        modifier = Modifier.weight(1f),
                        message = localizedString("No hay eventos con coordenadas para mostrar.")
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        key(mapInstance) {
                            AndroidView(
                                modifier = Modifier.fillMaxSize(),
                                factory = {
                                    WebView(context).apply {
                                        layoutParams = ViewGroup.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.MATCH_PARENT
                                        )
                                        setBackgroundColor(AndroidColor.WHITE)
                                        settings.javaScriptEnabled = true
                                        settings.allowFileAccess = false
                                        settings.allowContentAccess = false
                                        settings.domStorageEnabled = false
                                        settings.userAgentString =
                                            "${settings.userAgentString} NASAExplorer/1.0"
                                        // La selección vuelve por una URL local interceptada, sin exponer un bridge JS.
                                        webViewClient = object : WebViewClient() {
                                            override fun shouldOverrideUrlLoading(
                                                view: WebView,
                                                request: WebResourceRequest
                                            ): Boolean {
                                                val uri = request.url
                                                if (uri.host == MAP_BASE_HOST &&
                                                    uri.path == "/select"
                                                ) {
                                                    selectedEventId = uri.getQueryParameter("id")
                                                }
                                                return true
                                            }

                                            override fun onReceivedError(
                                                view: WebView,
                                                request: WebResourceRequest,
                                                error: android.webkit.WebResourceError
                                            ) {
                                                if (request.isForMainFrame) mapLoadFailed = true
                                            }
                                        }
                                        loadDataWithBaseURL(
                                            MAP_BASE_URL,
                                            createMapHtml(markersJson),
                                            "text/html",
                                            "UTF-8",
                                            null
                                        )
                                    }
                                }
                            )
                        }

                        if (mapLoadFailed) {
                            Card(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(24.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(localizedString("No se pudo cargar el mapa."))
                                    Button(onClick = {
                                        mapLoadFailed = false
                                        mapInstance++
                                    }) {
                                        Icon(Icons.Default.Refresh, contentDescription = null)
                                        Text(
                                            localizedString("Reintentar"),
                                            modifier = Modifier.padding(start = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    selectedPoint?.let { point ->
                        EventMapDetails(
                            point = point,
                            onClose = { selectedEventId = null }
                        )
                    } ?: Text(
                        localizedString(
                            "${points.size} eventos · toca un marcador para ver los detalles"
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    localizedString(
                        "Ubicaciones aproximadas según geometrías publicadas por NASA EONET. Mapa: OpenStreetMap."
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MapStatus(
    modifier: Modifier = Modifier,
    message: String,
    loading: Boolean = false,
    onRetry: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (loading) {
            CircularProgressIndicator()
        }
        Text(
            message,
            modifier = Modifier.padding(top = if (loading) 14.dp else 0.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        onRetry?.let { retry ->
            Button(onClick = retry, modifier = Modifier.padding(top = 12.dp)) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Text(localizedString("Reintentar"), modifier = Modifier.padding(start = 6.dp))
            }
        }
    }
}

@Composable
private fun EventMapDetails(point: EonetMapPoint, onClose: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .heightIn(max = 300.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    point.title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = onClose) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = localizedString("Cerrar detalle")
                    )
                }
            }
            EventDetailField(
                label = localizedString("Tipo de evento"),
                value = localizedEonetCategory(point.categoryId, point.category)
            )
            EventDetailField(
                label = localizedString("Fecha"),
                value = point.date.take(10).ifBlank {
                    localizedString("Fecha no disponible")
                }
            )
            val magnitude = point.magnitudeValue?.let { value ->
                "$value ${point.magnitudeUnit.orEmpty()}".trim()
            }
            if (magnitude != null) {
                EventDetailField(label = localizedString("Magnitud"), value = magnitude)
            }
            val eventDescription = point.description
            val categoryDescription = point.categoryDescription
            when {
                eventDescription != null -> EventDetailField(
                    label = localizedString("Descripción del evento"),
                    value = eventDescription
                )
                categoryDescription != null -> EventDetailField(
                    label = localizedString("Sobre este tipo de evento"),
                    value = localizedString(categoryDescription)
                )
                else -> Text(
                    localizedString("NASA EONET no publicó una descripción para este evento."),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (eventDescription == null && categoryDescription != null) {
                Text(
                    localizedString(
                        "La explicación de la categoría es general y no describe detalles específicos de este evento."
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EventDetailField(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(value, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun localizedEonetCategory(id: String, title: String): String = when (id) {
    "drought" -> localizedString("Sequía")
    "dustHaze" -> localizedString("Polvo y neblina")
    "earthquakes" -> localizedString("Terremotos")
    "floods" -> localizedString("Inundaciones")
    "landslides" -> localizedString("Deslizamientos de tierra")
    "manmade" -> localizedString("Eventos de origen humano")
    "seaLakeIce" -> localizedString("Hielo marino y lacustre")
    "severeStorms" -> localizedString("Tormentas severas")
    "snow" -> localizedString("Nieve")
    "tempExtremes" -> localizedString("Temperaturas extremas")
    "volcanoes" -> localizedString("Volcanes")
    "waterColor" -> localizedString("Cambios en el color del agua")
    "wildfires" -> localizedString("Incendios forestales")
    else -> title.ifBlank { localizedString("Evento natural") }
}

/** Builds a self-contained map page; only map tiles and the Leaflet library are remote. */
private fun createMapHtml(markersJson: String): String = """
    <!doctype html>
    <html>
    <head>
      <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0">
      <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css">
      <style>
        html, body, #map { height: 100%; margin: 0; background: #e8edf2; }
        #map-error {
          display: none; position: absolute; z-index: 1000; top: 12px; left: 12px; right: 12px;
          padding: 12px; border-radius: 12px; background: white; color: #172033;
          font: 14px sans-serif; box-shadow: 0 2px 8px #0003;
        }
        #map-error button {
          margin-left: 8px; padding: 6px 10px; border: 0; border-radius: 8px;
          background: #e65c35; color: white;
        }
      </style>
      <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
    </head>
    <body>
      <div id="map"></div>
      <div id="map-error">
        No se pudo cargar el mapa. Comprueba tu conexión a Internet.
        <button onclick="location.reload()">Reintentar</button>
      </div>
      <script>
        const events = $markersJson;
        if (!window.L) {
          document.getElementById('map-error').style.display = 'block';
        } else {
          const map = L.map('map', { zoomControl: true }).setView([15, 0], 2);
          const tiles = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            maxZoom: 18,
            attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
          }).addTo(map);
          tiles.on('tileerror', () => {
            document.getElementById('map-error').style.display = 'block';
          });
          const bounds = [];
          events.forEach(event => {
            const position = [event.latitude, event.longitude];
            const marker = L.circleMarker(position, {
              radius: 8, color: '#ffffff', weight: 2, fillColor: '#e65c35', fillOpacity: 0.9
            }).addTo(map);
            const label = document.createElement('span');
            label.textContent = event.title;
            marker.bindTooltip(label);
            marker.on('click', () => {
              window.location.href = 'https://nasa-explorer.local/select?id=' +
                encodeURIComponent(event.id);
            });
            bounds.push(position);
          });
          if (bounds.length > 1) map.fitBounds(bounds, { padding: [24, 24], maxZoom: 6 });
          if (bounds.length === 1) map.setView(bounds[0], 5);
        }
      </script>
    </body>
    </html>
""".trimIndent()

private const val MAP_BASE_HOST = "nasa-explorer.local"
private const val MAP_BASE_URL = "https://$MAP_BASE_HOST/"
