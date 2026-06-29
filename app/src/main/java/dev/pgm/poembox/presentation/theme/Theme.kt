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
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    onError = md_theme_dark_onError,
    errorContainer = md_theme_dark_errorContainer,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    surfaceContainer = md_theme_dark_surfaceContainer,
    surfaceContainerHigh = md_theme_dark_surfaceContainerHigh,
    outline = md_theme_dark_outline,
    outlineVariant = md_theme_dark_outlineVariant,
    inverseSurface = md_theme_dark_inverseSurface,
    inverseOnSurface = md_theme_dark_inverseOnSurface
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
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    onError = md_theme_light_onError,
    errorContainer = md_theme_light_errorContainer,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    surfaceContainer = md_theme_light_surfaceContainer,
    surfaceContainerHigh = md_theme_light_surfaceContainerHigh,
    outline = md_theme_light_outline,
    outlineVariant = md_theme_light_outlineVariant,
    inverseSurface = md_theme_light_inverseSurface,
    inverseOnSurface = md_theme_light_inverseOnSurface
)

enum class PoemBoxThemeMode {
    LIGHT, DARK, SEPIA, MIDNIGHT
}

private val SepiaColorScheme = lightColorScheme(
    primary = sepia_primary,
    onPrimary = sepia_onPrimary,
    primaryContainer = sepia_primaryContainer,
    onPrimaryContainer = sepia_onPrimaryContainer,
    secondary = sepia_secondary,
    onSecondary = sepia_onSecondary,
    secondaryContainer = sepia_secondaryContainer,
    onSecondaryContainer = sepia_onSecondaryContainer,
    error = sepia_error,
    onError = sepia_onError,
    background = sepia_background,
    onBackground = sepia_onBackground,
    surface = sepia_surface,
    onSurface = sepia_onSurface,
    surfaceVariant = sepia_surfaceVariant,
    onSurfaceVariant = sepia_onSurfaceVariant,
    surfaceContainer = sepia_surfaceVariant,
    outline = sepia_outline
)

private val MidnightColorScheme = darkColorScheme(
    primary = midnight_primary,
    onPrimary = midnight_onPrimary,
    primaryContainer = midnight_primaryContainer,
    onPrimaryContainer = midnight_onPrimaryContainer,
    secondary = midnight_secondary,
    onSecondary = midnight_onSecondary,
    secondaryContainer = midnight_secondaryContainer,
    onSecondaryContainer = midnight_onSecondaryContainer,
    error = midnight_error,
    onError = midnight_onError,
    background = midnight_background,
    onBackground = midnight_onBackground,
    surface = midnight_surface,
    onSurface = midnight_onSurface,
    surfaceVariant = midnight_surfaceVariant,
    onSurfaceVariant = midnight_onSurfaceVariant,
    surfaceContainer = midnight_surfaceVariant,
    outline = midnight_outline
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
