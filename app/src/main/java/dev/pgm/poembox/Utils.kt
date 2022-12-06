package dev.pgm.poembox

import androidx.compose.runtime.Composable
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun ShowBars(flag: Boolean) {
    rememberSystemUiController().apply {

        this.isSystemBarsVisible = flag
        this.isStatusBarVisible = flag
        this.isNavigationBarVisible = flag
    }
}