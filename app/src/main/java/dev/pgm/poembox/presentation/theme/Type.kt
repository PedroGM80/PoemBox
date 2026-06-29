package dev.pgm.poembox.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import dev.pgm.poembox.R

/**
 * PoeticFont (Cormorant Garamond) — fuente serif decorativa.
 * Se reserva para títulos grandes y el TEXTO POÉTICO (poemas, lectura),
 * donde su elegancia luce. NO se usa en controles de UI porque su línea
 * base no centra bien en botones/chips y se ve desalineada.
 */
val PoeticFont = FontFamily(
    Font(R.font.cormorant_garamond_regular, weight = FontWeight.Normal),
    Font(R.font.cormorant_garamond_bold,    weight = FontWeight.Bold),
    Font(R.font.cormorant_garamond_italic,  weight = FontWeight.Normal, style = FontStyle.Italic)
)

/**
 * UiFont — fuente del sistema (sans-serif). Limpia y legible, se alinea
 * perfectamente en botones, navegación, etiquetas y cuerpo de UI.
 */
val UiFont = FontFamily.Default

// Centrado vertical correcto del texto (evita el padding asimétrico de fuentes).
private val balancedPlatform = PlatformTextStyle(includeFontPadding = false)
private val centeredLineHeight = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.None
)

val Typography = Typography(
    // ── Display (PoeticFont) — portada, marca, grandes titulares ──
    displayLarge = TextStyle(
        fontFamily = PoeticFont, fontWeight = FontWeight.Bold,
        fontSize = 30.sp, lineHeight = 38.sp, letterSpacing = (-0.5).sp,
        platformStyle = balancedPlatform, lineHeightStyle = centeredLineHeight
    ),
    displayMedium = TextStyle(
        fontFamily = PoeticFont, fontWeight = FontWeight.Bold,
        fontSize = 26.sp, lineHeight = 34.sp, letterSpacing = 0.sp,
        platformStyle = balancedPlatform, lineHeightStyle = centeredLineHeight
    ),
    displaySmall = TextStyle(
        fontFamily = PoeticFont, fontWeight = FontWeight.Bold,
        fontSize = 22.sp, lineHeight = 30.sp, letterSpacing = 0.sp,
        platformStyle = balancedPlatform, lineHeightStyle = centeredLineHeight
    ),

    // ── Headline (PoeticFont) — encabezados de sección ──
    headlineLarge = TextStyle(
        fontFamily = PoeticFont, fontWeight = FontWeight.Bold,
        fontSize = 22.sp, lineHeight = 30.sp, letterSpacing = 0.sp,
        platformStyle = balancedPlatform, lineHeightStyle = centeredLineHeight
    ),
    headlineMedium = TextStyle(
        fontFamily = PoeticFont, fontWeight = FontWeight.Bold,
        fontSize = 20.sp, lineHeight = 28.sp, letterSpacing = 0.sp,
        platformStyle = balancedPlatform, lineHeightStyle = centeredLineHeight
    ),
    headlineSmall = TextStyle(
        fontFamily = PoeticFont, fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp, lineHeight = 26.sp, letterSpacing = 0.sp,
        platformStyle = balancedPlatform, lineHeightStyle = centeredLineHeight
    ),

    // ── Title — titleLarge poético (títulos de poemas); resto en UiFont ──
    titleLarge = TextStyle(
        fontFamily = PoeticFont, fontWeight = FontWeight.Bold,
        fontSize = 20.sp, lineHeight = 28.sp, letterSpacing = 0.sp,
        platformStyle = balancedPlatform, lineHeightStyle = centeredLineHeight
    ),
    titleMedium = TextStyle(
        fontFamily = UiFont, fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.15.sp,
        platformStyle = balancedPlatform, lineHeightStyle = centeredLineHeight
    ),
    titleSmall = TextStyle(
        fontFamily = UiFont, fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp,
        platformStyle = balancedPlatform, lineHeightStyle = centeredLineHeight
    ),

    // ── Body — bodyLarge poético (lectura del poema); resto en UiFont ──
    bodyLarge = TextStyle(
        fontFamily = PoeticFont, fontWeight = FontWeight.Normal,
        fontSize = 18.sp, lineHeight = 28.sp, letterSpacing = 0.3.sp,
        platformStyle = balancedPlatform, lineHeightStyle = centeredLineHeight
    ),
    bodyMedium = TextStyle(
        fontFamily = UiFont, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.25.sp,
        platformStyle = balancedPlatform, lineHeightStyle = centeredLineHeight
    ),
    bodySmall = TextStyle(
        fontFamily = UiFont, fontWeight = FontWeight.Normal,
        fontSize = 13.sp, lineHeight = 18.sp, letterSpacing = 0.25.sp,
        platformStyle = balancedPlatform, lineHeightStyle = centeredLineHeight
    ),

    // ── Label (UiFont) — botones, navegación, chips, contadores ──
    labelLarge = TextStyle(
        fontFamily = UiFont, fontWeight = FontWeight.Medium,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp,
        platformStyle = balancedPlatform, lineHeightStyle = centeredLineHeight
    ),
    labelMedium = TextStyle(
        fontFamily = UiFont, fontWeight = FontWeight.Medium,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp,
        platformStyle = balancedPlatform, lineHeightStyle = centeredLineHeight
    ),
    labelSmall = TextStyle(
        fontFamily = UiFont, fontWeight = FontWeight.Medium,
        fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp,
        platformStyle = balancedPlatform, lineHeightStyle = centeredLineHeight
    )
)
