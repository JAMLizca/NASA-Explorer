package com.example.nasaexplorer.presentation.detail

import android.text.Html
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.nasaexplorer.data.remote.apod.ApodDto

@Composable
fun ApodDetailScreen(
    apod: ApodDto,
    onBack: () -> Unit
) {
    val explanation = Html.fromHtml(
        apod.explanation ?: "No hay explicación disponible.",
        Html.FROM_HTML_MODE_COMPACT
    ).toString().trim()

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
            text = apod.title ?: "Astronomía del día",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = apod.date ?: "Fecha no disponible",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (apod.mediaType == "image" && !apod.url.isNullOrBlank()) {
            AsyncImage(
                model = apod.hdUrl ?: apod.url,
                contentDescription = apod.alt ?: apod.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 180.dp, max = 350.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Descripción",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = explanation,
            style = MaterialTheme.typography.bodyLarge
        )

        if (!apod.credit.isNullOrBlank() || !apod.copyright.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Créditos: ${apod.credit ?: apod.copyright}",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}
