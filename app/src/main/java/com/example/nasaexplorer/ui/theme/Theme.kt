package com.example.nasaexplorer.ui.theme

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView

private val LightColorScheme = lightColorScheme(
    primary = SpaceBlue,
    secondary = SpaceCyan,
    tertiary = SpacePurple,

    background = LightBackground,
    surface = LightSurface,

    onPrimary = Color.White,
    onSecondary = Color(0xFF0F172A),
    onBackground = LightText,
    onSurface = LightText,
)

private val DarkColorScheme = darkColorScheme(
    primary = SpaceBlueDark,
    secondary = SpaceCyan,
    tertiary = SpacePurple,

    background = DarkBackground,
    surface = DarkSurface,

    onPrimary = Color.White,
    onSecondary = Color(0xFF0F172A),
    onBackground = DarkText,
    onSurface = DarkText,
)

@Composable
fun NASAExplorerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    val view = LocalView.current

    if (!view.isInEditMode) {
        LocalActivity.current?.window?.let { window ->
            window.statusBarColor = android.graphics.Color.parseColor(
                if (darkTheme) "#0B1220" else "#F1F5F9"
            )
            window.navigationBarColor = window.statusBarColor
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
