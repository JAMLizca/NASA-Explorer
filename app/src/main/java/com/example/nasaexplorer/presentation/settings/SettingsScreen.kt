/** Construye la pantalla principal y conecta la interfaz con su estado. */
package com.example.nasaexplorer.presentation.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import com.example.nasaexplorer.ui.components.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.nasaexplorer.BuildConfig
import com.example.nasaexplorer.R
import com.example.nasaexplorer.ui.components.localizedString
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
/** Dibuja en Compose la interfaz asociada a settings screen */
fun SettingsScreen(
    themeViewModel: ThemeViewModel
) {
    val themeMode by themeViewModel.themeMode.collectAsState()
    val appLanguage by themeViewModel.appLanguage.collectAsState()
    var showAboutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(22.dp))
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Icon(
                Icons.Default.Settings,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(14.dp)
            )
        }
        Spacer(Modifier.height(14.dp))
        Text(
            text = "Ajustes",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            "Personaliza tu experiencia de exploración.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        Text("APARIENCIA", style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(10.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                ThemeOption(
                    title = "Claro",
                    subtitle = "Superficies luminosas durante el día",
                    icon = Icons.Default.WbSunny,
                    selected = themeMode == "LIGHT",
                    onClick = { themeViewModel.changeTheme("LIGHT") }
                )
                ThemeOption(
                    title = "Oscuro",
                    subtitle = "Una experiencia cómoda en la noche",
                    icon = Icons.Default.DarkMode,
                    selected = themeMode == "DARK",
                    onClick = { themeViewModel.changeTheme("DARK") }
                )
                ThemeOption(
                    title = "Seguir el sistema",
                    subtitle = "Usa la preferencia del dispositivo",
                    icon = Icons.Default.BrightnessAuto,
                    selected = themeMode == "SYSTEM",
                    onClick = { themeViewModel.changeTheme("SYSTEM") }
                )
            }
        }
        Spacer(Modifier.height(22.dp))
        Text(
            "IDIOMA",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(10.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Idioma de la aplicación",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    "Elige el idioma de todos los menús y controles.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilterChip(
                        selected = appLanguage == "es",
                        onClick = { themeViewModel.changeLanguage("es") },
                        label = { Text("Español") }
                    )
                    FilterChip(
                        selected = appLanguage == "en",
                        onClick = { themeViewModel.changeLanguage("en") },
                        label = { Text("English") }
                    )
                }
            }
        }
        Spacer(Modifier.height(22.dp))
        Card(
            onClick = { showAboutDialog = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(11.dp)
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                ) {
                    Text(
                        "Acerca de NASA Explorer",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        "Versión ${BuildConfig.VERSION_NAME} · Créditos y fuentes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = localizedString("Ver información de la aplicación"),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (showAboutDialog) {
        AboutDialog(onDismissRequest = { showAboutDialog = false })
    }
}

@Composable
private fun AboutDialog(onDismissRequest: () -> Unit) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 520.dp)
                .heightIn(max = (LocalConfiguration.current.screenHeightDp * 0.88f).dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            shadowElevation = 24.dp
        ) {
            Column {
                AboutHero(onDismissRequest)
                // El contenido central puede crecer con el tamaño de letra sin sacar el cierre de pantalla.
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    AboutSection(
                        icon = Icons.Default.AutoAwesome,
                        title = "Una herramienta para explorar",
                        description = "Explora imágenes astronómicas, asteroides cercanos y eventos de nuestro planeta con datos publicados por NASA."
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            "Fuentes de información",
                            style = MaterialTheme.typography.titleSmall
                        )
                        SourceItem("NASA APOD", "Imágenes y explicaciones astronómicas")
                        SourceItem("NASA NeoWs", "Información de objetos cercanos a la Tierra")
                        SourceItem("NASA EONET y DONKI", "Eventos naturales y actividad espacial")
                        SourceItem("NASA GIBS", "Imágenes satelitales de la Tierra")
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "NASA proporciona los datos astronómicos. Esta aplicación es un proyecto independiente y no representa una afiliación oficial con NASA.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                TextButton(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Cerrar")
                }
            }
        }
    }
}

@Composable
private fun AboutHero(onDismissRequest: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF071932),
                        Color(0xFF0B3D91),
                        Color(0xFF006B7A)
                    )
                )
            )
            .padding(start = 20.dp, end = 12.dp, top = 18.dp, bottom = 22.dp)
    ) {
        IconButton(
            onClick = onDismissRequest,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = localizedString("Cerrar"),
                tint = Color.White
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Image(
                painter = painterResource(R.mipmap.nasa_explorer_launcher),
                contentDescription = localizedString("Logotipo de NASA Explorer"),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "NASA Explorer",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
                Text(
                    "Observa · comprende · explora",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.86f)
                )
            }
            Surface(
                shape = RoundedCornerShape(50),
                color = Color.White.copy(alpha = 0.14f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color.White.copy(alpha = 0.28f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Public,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        "Versión ${BuildConfig.VERSION_NAME}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun AboutSection(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(10.dp)
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SourceItem(name: String, description: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Public,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(name, style = MaterialTheme.typography.labelLarge)
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ThemeOption(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(10.dp)
                )
            }
            Column(Modifier.padding(start = 12.dp)) {
                Text(text = title, style = MaterialTheme.typography.titleSmall)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        RadioButton(
            selected = selected,
            onClick = onClick
        )
    }
}
