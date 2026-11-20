/** Construye la pantalla principal y conecta la interfaz con su estado. */
package com.example.nasaexplorer.presentation.detail

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.nasaexplorer.ui.components.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.nasaexplorer.data.remote.apod.ApodDto
import com.example.nasaexplorer.ui.components.localizedString
import com.example.nasaexplorer.ui.theme.SpaceBlue
import com.example.nasaexplorer.ui.theme.SpaceCyan
import com.example.nasaexplorer.ui.theme.SpaceCyanBright
import com.example.nasaexplorer.ui.theme.SpaceNavy

@Composable
/** Dibuja en Compose la interfaz asociada a apod detail screen */
fun ApodDetailScreen(
    apod: ApodDto,
    onBack: () -> Unit
) {
    val explanation = Html.fromHtml(
        apod.explanation ?: "No hay explicación disponible.",
        Html.FROM_HTML_MODE_COMPACT
    ).toString().trim()
    val credit = Html.fromHtml(
        apod.credit ?: apod.copyright.orEmpty(),
        Html.FROM_HTML_MODE_COMPACT
    ).toString().trim()
    val mediaUrl = (apod.basicHtmlUrl ?: apod.permalink ?: apod.url)
        ?.takeIf(String::isNotBlank)
    val uriHandler = LocalUriHandler.current
    val isImage = apod.mediaType == "image" && !apod.hdUrl.isNullOrBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
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
                    contentDescription = localizedString("Volver a APOD")
                )
            }
            Column(Modifier.weight(1f)) {
                Text(
                    "DETALLE ASTRONÓMICO",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Astronomy Picture of the Day",
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

        if (isImage) {
            AsyncImage(
                model = apod.hdUrl,
                contentDescription = apod.alt ?: apod.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(270.dp)
                    .clip(RoundedCornerShape(24.dp))
            )
        } else {
            MediaPlaceholder(
                mediaType = apod.mediaType ?: "tipo no especificado",
                isVideo = apod.mediaType == "video"
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            apod.date ?: "Fecha no disponible",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(7.dp))
        Text(
            apod.title ?: "Astronomía del día",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(22.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
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
                modifier = Modifier.padding(19.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "La historia detrás de la imagen",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    explanation,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (credit.isNotBlank()) {
            Spacer(Modifier.height(14.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        "CRÉDITOS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        credit,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (!isImage && mediaUrl != null) {
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { uriHandler.openUri(mediaUrl) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    "Abrir contenido multimedia",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Spacer(Modifier.height(112.dp))
    }
}

@Composable
private fun MediaPlaceholder(
    mediaType: String,
    isVideo: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    listOf(SpaceNavy, SpaceBlue, SpaceCyan)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.14f)
            ) {
                Icon(
                    if (isVideo) Icons.Default.PlayCircle else Icons.Default.Image,
                    contentDescription = null,
                    tint = SpaceCyanBright,
                    modifier = Modifier
                        .padding(15.dp)
                        .size(34.dp)
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                if (isVideo) "Contenido en video" else "Contenido multimedia",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                mediaType,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.82f)
            )
        }
    }
}
