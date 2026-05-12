package dev.pgm.poembox.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.pgm.poembox.domain.ShowBars
import dev.pgm.poembox.presentation.screens.SetUpNavController
import dev.pgm.poembox.presentation.theme.PoemBoxTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object {
        internal var POEM_TITLE = ""
        internal var VALIDATE_STATUS = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PoemBoxTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    ShowBars(flag = false)
                    SetUpNavController(rememberNavController())
                }
            }
        }
    }
}
