package com.example.nasaexplorer.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SettingsScreen(
    themeViewModel: ThemeViewModel
) {
    val themeMode by themeViewModel.themeMode.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Configuración",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Apariencia",
            style = MaterialTheme.typography.titleMedium
        )

        ThemeOption(
            title = "Claro",
            selected = themeMode == "LIGHT",
            onClick = { themeViewModel.changeTheme("LIGHT") }
        )

        ThemeOption(
            title = "Oscuro",
            selected = themeMode == "DARK",
            onClick = { themeViewModel.changeTheme("DARK") }
        )

        ThemeOption(
            title = "Seguir el sistema",
            selected = themeMode == "SYSTEM",
            onClick = { themeViewModel.changeTheme("SYSTEM") }
        )
    }
}

@Composable
private fun ThemeOption(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title)

        RadioButton(
            selected = selected,
            onClick = onClick
        )
    }
}
