package dev.pgm.poembox.presentation.theme

import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)

val ColorPoemField: TextFieldColors
    @Composable
    get() = TextFieldDefaults.textFieldColors(
        textColor = Color.Black,
        focusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        backgroundColor = Color.LightGray,
    )

val ColorPoemFieldDialog: TextFieldColors
    @Composable
    get() = TextFieldDefaults.textFieldColors(
        textColor = Color.Black,
        focusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        backgroundColor = Color.Transparent,
    )