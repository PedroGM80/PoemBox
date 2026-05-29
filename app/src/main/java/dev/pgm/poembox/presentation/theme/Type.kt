package dev.pgm.poembox.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.pgm.poembox.R

/**
 * PoeticFont usa Cormorant Garamond desde los assets locales (res/font/).
 * Esto garantiza que la fuente carga aunque no haya conexión a internet,
 * evitando el uso del sistema en el primer arranque sin red.
 */
val PoeticFont = FontFamily(
    Font(R.font.cormorant_garamond_regular, weight = FontWeight.Normal),
    Font(R.font.cormorant_garamond_bold,    weight = FontWeight.Bold),
    Font(R.font.cormorant_garamond_italic,  weight = FontWeight.Normal, style = FontStyle.Italic)
)

val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = PoeticFont,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = PoeticFont,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = PoeticFont,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = PoeticFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    )
)
