/** Construye el diálogo de asistencia y gestiona su interacción. */
package com.example.nasaexplorer.presentation.assistant

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import com.example.nasaexplorer.ui.components.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nasaexplorer.data.local.AppDatabase
import com.example.nasaexplorer.data.local.entity.AssistantHistoryEntity
import com.example.nasaexplorer.data.repository.AssistantHistoryRepository
import com.example.nasaexplorer.data.repository.AiRepository
import com.example.nasaexplorer.ui.components.localizedString
import com.example.nasaexplorer.ui.theme.SpaceCyanBright
import com.example.nasaexplorer.ui.theme.SpaceNavy
import java.text.DateFormat
import java.util.Date
import java.util.Locale

@Composable
/** Dibuja en Compose la interfaz asociada a assistant dialog */
fun AssistantDialog(
    context: Map<String, String>,
    onDismiss: () -> Unit
) {
    val appContext = LocalContext.current.applicationContext
    val assistantViewModel: AssistantViewModel = viewModel(
        key = "assistant-${context["id"].orEmpty()}",
        factory = remember(appContext) {
            AssistantViewModelFactory(
                AiRepository(),
                AssistantHistoryRepository(
                    AppDatabase.getDatabase(appContext).assistantHistoryDao()
                )
            )
        }
    )
    val state by assistantViewModel.uiState.collectAsState()
    val history by assistantViewModel.history.collectAsState()
    val historyError by assistantViewModel.historyError.collectAsState()
    var question by remember { mutableStateOf("") }
    var showingHistory by remember { mutableStateOf(false) }
    var selectedHistoryId by remember { mutableStateOf<Long?>(null) }
    val topic = context["type"].orEmpty()
    val contextTitle = context["title"] ?: context["name"]
    val language = LocalConfiguration.current.locales[0]?.language ?: "es"
    val contextLabel = when (topic) {
        "APOD" -> "IMAGEN DEL DÍA"
        "ASTEROID", "NEOWS" -> "ASTEROIDES CERCANOS"
        else -> "EXPLORACIÓN ESPACIAL"
    }

    /** Implementa el comportamiento o contenido asociado a submit question. */
    fun submitQuestion(prompt: String = question) {
        val trimmedPrompt = prompt.trim()
        if (trimmedPrompt.isNotEmpty() && state !is AssistantUiState.Loading) {
            assistantViewModel.ask(trimmedPrompt, context + ("response_language" to language))
            question = ""
            showingHistory = false
            selectedHistoryId = null
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 520.dp)
                .heightIn(max = (LocalConfiguration.current.screenHeightDp * 0.88f).dp),
                shape = RoundedCornerShape(30.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 10.dp,
            shadowElevation = 28.dp
        ) {
            Column {
                AssistantHeader(
                    contextLabel = contextLabel,
                    contextTitle = contextTitle,
                    onDismiss = onDismiss
                )

                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .heightIn(min = 170.dp, max = 390.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (showingHistory) {
                        AssistantHistoryPanel(
                            entries = history,
                            selectedEntryId = selectedHistoryId,
                            language = language,
                            onSelectEntry = { selectedHistoryId = it },
                            onBack = { selectedHistoryId = null },
                            onDeleteEntry = { id ->
                                assistantViewModel.deleteHistoryEntry(id)
                                if (selectedHistoryId == id) selectedHistoryId = null
                            },
                            onDeleteAll = {
                                assistantViewModel.deleteAllHistory()
                                selectedHistoryId = null
                            }
                        )
                    } else if (state is AssistantUiState.Idle) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                                ) {
                                    Icon(
                                        Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(10.dp).size(20.dp)
                                    )
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                    Text(
                                        "¿Qué te gustaría descubrir?",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        if (contextTitle != null) {
                                            "Puedo ayudarte a entender la información de NASA que estás viendo."
                                        } else {
                                            "Pregunta sobre astronomía o conceptos espaciales. Si no hay datos de un objeto seleccionado, te lo indicaré."
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                            Text(
                                "PREGUNTAS SUGERIDAS",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        val suggestions = when (topic) {
                            "APOD" -> listOf(
                                if (language == "en") "Explain this image" else "Explícame esta imagen",
                                if (language == "en") "What astronomical terms appear here?" else "¿Qué términos astronómicos aparecen?"
                            )
                            "ASTEROID", "NEOWS" -> listOf(
                                if (language == "en") "Explain these measurements" else "Explícame estos datos",
                                if (language == "en") "What does potentially hazardous mean?" else "¿Qué significa potencialmente peligroso?"
                            )
                            else -> listOf(
                                if (language == "en") "What is a light-year?" else "¿Qué es un año luz?",
                                if (language == "en") "How are asteroids studied?" else "¿Cómo se estudian los asteroides?"
                            )
                        }
                        suggestions.forEach { suggestion ->
                            OutlinedButton(
                                onClick = {
                                    question = suggestion
                                    submitQuestion(suggestion)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                    horizontal = 14.dp,
                                    vertical = 13.dp
                                ),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onSurface,
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.75f)
                                )
                            ) {
                                Text(
                                    text = suggestion,
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    when (val current = state) {
                        AssistantUiState.Idle -> Unit
                        is AssistantUiState.Loading -> {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(17.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(13.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(23.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                        Text(
                                            "Preparando una explicación…",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            "Revisando el contexto disponible",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                        is AssistantUiState.Success -> {
                            Surface(
                                modifier = Modifier.align(Alignment.End),
                                shape = RoundedCornerShape(
                                    topStart = 18.dp,
                                    topEnd = 18.dp,
                                    bottomStart = 18.dp,
                                    bottomEnd = 5.dp
                                ),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = current.question,
                                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 12.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(
                                    topStart = 5.dp,
                                    topEnd = 20.dp,
                                    bottomStart = 20.dp,
                                    bottomEnd = 20.dp
                                ),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f)
                                )
                            ) {
                                Column(
                                    Modifier.padding(17.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            "RESPUESTA",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        current.answer,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.15f
                                    )
                                }
                            }
                        }
                        is AssistantUiState.Error -> {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                color = MaterialTheme.colorScheme.errorContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        current.message,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }

                historyError?.let { message ->
                    Text(
                        text = message,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                AssistantComposer(
                    question = question,
                    onQuestionChange = { question = it },
                    enabled = state !is AssistantUiState.Loading,
                    onSend = { submitQuestion() },
                    historyCount = history.size,
                    showingHistory = showingHistory,
                    onHistoryClick = {
                        if (showingHistory) {
                            showingHistory = false
                            selectedHistoryId = null
                        } else {
                            showingHistory = true
                            selectedHistoryId = null
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun AssistantHistoryPanel(
    entries: List<AssistantHistoryEntity>,
    selectedEntryId: Long?,
    language: String,
    onSelectEntry: (Long) -> Unit,
    onBack: () -> Unit,
    onDeleteEntry: (Long) -> Unit,
    onDeleteAll: () -> Unit
) {
    var confirmDeleteAll by remember { mutableStateOf(false) }
    val selectedEntry = entries.firstOrNull { it.id == selectedEntryId }
    if (confirmDeleteAll) {
        AlertDialog(
            onDismissRequest = { confirmDeleteAll = false },
            title = { Text("¿Borrar todo el historial?") },
            text = { Text("Se eliminarán todas las preguntas y respuestas guardadas en este dispositivo.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteAll()
                        confirmDeleteAll = false
                    }
                ) {
                    Text("Borrar todo")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDeleteAll = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
    if (selectedEntry != null) {
        OutlinedButton(
            onClick = onBack,
            shape = RoundedCornerShape(14.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = 12.dp,
                vertical = 8.dp
            )
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = null)
            Text("Volver al historial", modifier = Modifier.padding(start = 8.dp))
        }
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Text(
                text = selectedEntry.question,
                modifier = Modifier.padding(15.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                Text(
                    "RESPUESTA",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    selectedEntry.answer,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                Icons.Default.History,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                "Historial de preguntas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.weight(1f))
            if (entries.isNotEmpty()) {
                TextButton(onClick = { confirmDeleteAll = true }) {
                    Icon(
                        Icons.Default.DeleteSweep,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        "Borrar todo",
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
        if (entries.isEmpty()) {
            Text(
                "Tus preguntas respondidas aparecerán aquí.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            val dateFormatter = remember(language) {
                DateFormat.getDateTimeInstance(
                    DateFormat.MEDIUM,
                    DateFormat.SHORT,
                    Locale.forLanguageTag(language)
                )
            }
            entries.forEach { entry ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    OutlinedButton(
                        onClick = { onSelectEntry(entry.id) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            horizontal = 14.dp,
                            vertical = 12.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                entry.question,
                                maxLines = 2,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                dateFormatter.format(Date(entry.createdAt)),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { onDeleteEntry(entry.id) }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = localizedString("Eliminar pregunta del historial"),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AssistantHeader(
    contextLabel: String,
    contextTitle: String?,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        SpaceNavy,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.96f)
                    )
                )
            )
            .padding(start = 21.dp, top = 20.dp, end = 13.dp, bottom = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.12f)
            ) {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = SpaceCyanBright,
                    modifier = Modifier.padding(10.dp).size(22.dp)
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    "Asistente NASA",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "Explicaciones con contexto NASA",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.78f)
                )
            }
            IconButton(onClick = onDismiss) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.12f)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = localizedString("Cerrar asistente"),
                        tint = Color.White,
                        modifier = Modifier.padding(8.dp).size(20.dp)
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White.copy(alpha = 0.10f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(SpaceCyanBright)
                )
                Text(
                    contextTitle?.let { "$contextLabel · $it" } ?: contextLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun AssistantComposer(
    question: String,
    onQuestionChange: (String) -> Unit,
    enabled: Boolean,
    onSend: () -> Unit,
    historyCount: Int,
    showingHistory: Boolean,
    onHistoryClick: () -> Unit
) {
    val canSend = question.isNotBlank() && enabled

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.32f))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = question,
                onValueChange = onQuestionChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Escribe tu pregunta…") },
                enabled = enabled,
                shape = RoundedCornerShape(18.dp),
                maxLines = 3,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (canSend) onSend()
                })
            )
            Surface(
                shape = CircleShape,
                color = if (canSend) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            ) {
                IconButton(
                    onClick = onSend,
                    enabled = canSend
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = localizedString("Enviar pregunta"),
                        tint = if (canSend) {
                            Color.White
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(7.dp))
        Text(
            "Respuestas orientativas basadas en el contexto. No predice impactos.",
            modifier = Modifier.padding(start = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedButton(
            onClick = onHistoryClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = 12.dp,
                vertical = 8.dp
            )
        ) {
            Icon(
                if (showingHistory) Icons.Default.ArrowBack else Icons.Default.History,
                contentDescription = null
            )
            Text(
                text = if (showingHistory) "Volver al chat" else "Historial de preguntas",
                modifier = Modifier.padding(start = 8.dp)
            )
            if (!showingHistory) {
                Text(
                    "($historyCount)",
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}
