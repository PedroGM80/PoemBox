package dev.pgm.poembox.domain

import androidx.compose.runtime.Composable
import com.google.accompanist.systemuicontroller.rememberSystemUiController

/**
 * Show bars
 *
 * @param flag
 */
@Composable
fun ShowBars(flag: Boolean) {
    rememberSystemUiController().apply {

        this.isSystemBarsVisible = flag
        this.isStatusBarVisible = flag
        this.isNavigationBarVisible = flag
    }
}