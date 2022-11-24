package dev.pgm.poembox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import dev.pgm.poembox.screens.SetUpNavController
import dev.pgm.poembox.ui.theme.PoemBoxTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PoemBoxTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    //set up nav controller
                    ShowBars(flag = false)
                    SetUpNavController(rememberNavController())
                }
            }
        }
    }

    fun createUserTable() {

    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PoemBoxTheme {
        SetUpNavController(rememberNavController())
    }
}