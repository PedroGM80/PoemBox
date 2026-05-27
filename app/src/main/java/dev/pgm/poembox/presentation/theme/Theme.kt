package dev.pgm.poembox.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface
)

private val LightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface
)

enum class PoemBoxThemeMode {
    LIGHT, DARK, SEPIA, MIDNIGHT
}

private val SepiaColorScheme = lightColorScheme(
    primary = Color(0xFF704214), // Sepia Brown
    onPrimary = Color.White,
    background = Color(0xFFFAF3E0), // Old Paper
    surface = Color(0xFFFAF3E0),
    onBackground = Color(0xFF3E2723),
    onSurface = Color(0xFF3E2723),
    secondary = Color(0xFF5D4037)
)

private val MidnightColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),
    onPrimary = Color.Black,
    background = Color(0xFF121212), // Deep Ink
    surface = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFE0E0E0),
    secondary = Color(0xFF03DAC6)
)

@Composable
fun PoemBoxTheme(
    themeMode: PoemBoxThemeMode = if (isSystemInDarkTheme()) PoemBoxThemeMode.DARK else PoemBoxThemeMode.LIGHT,
    content: @Composable () -> Unit
) {
    val colors = when (themeMode) {
        PoemBoxThemeMode.LIGHT -> LightColorScheme
        PoemBoxThemeMode.DARK -> DarkColorScheme
        PoemBoxThemeMode.SEPIA -> SepiaColorScheme
        PoemBoxThemeMode.MIDNIGHT -> MidnightColorScheme
    }
    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
