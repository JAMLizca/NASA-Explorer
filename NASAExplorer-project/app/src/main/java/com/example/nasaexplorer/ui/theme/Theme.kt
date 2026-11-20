/** Define los elementos visuales compartidos del tema de la aplicación. */
package com.example.nasaexplorer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = SpaceBlue,
    secondary = SpaceCyan,
    tertiary = NasaRed,
    error = NasaRed,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    primaryContainer = Color(0xFFE3ECFA),
    secondaryContainer = Color(0xFFDFF5F6),
    tertiaryContainer = Color(0xFFFBE8E7),
    errorContainer = Color(0xFFFBE8E7),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onError = Color.White,
    onBackground = LightText,
    onSurface = LightText,
    onSurfaceVariant = LightSecondaryText,
    onPrimaryContainer = Color(0xFF102A4E),
    onSecondaryContainer = Color(0xFF103941),
    onTertiaryContainer = Color(0xFF641C18),
    onErrorContainer = Color(0xFF641C18),
)

private val DarkColorScheme = darkColorScheme(
    primary = SpaceBlueDark,
    secondary = SpaceCyanBright,
    tertiary = NasaRedDark,
    error = NasaRedDark,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    primaryContainer = Color(0xFF17335C),
    secondaryContainer = Color(0xFF103640),
    tertiaryContainer = Color(0xFF542421),
    errorContainer = Color(0xFF542421),
    onPrimary = Color(0xFF08244A),
    onSecondary = Color(0xFF062B33),
    onTertiary = Color(0xFF3B0B08),
    onError = Color(0xFF3B0B08),
    onBackground = DarkText,
    onSurface = DarkText,
    onSurfaceVariant = DarkSecondaryText,
    onPrimaryContainer = Color(0xFFD5E9FF),
    onSecondaryContainer = Color(0xFFC7F1F4),
    onTertiaryContainer = Color(0xFFFFE1DE),
    onErrorContainer = Color(0xFFFFE1DE),
)

/** Compone la interfaz asociada a esta sección de la pantalla. */
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
