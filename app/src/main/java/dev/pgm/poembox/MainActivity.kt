package dev.pgm.poembox

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.rememberNavController
import dev.pgm.poembox.CryptoManager.Companion.ALIAS
import dev.pgm.poembox.screens.SetUpNavController
import dev.pgm.poembox.ui.theme.PoemBoxTheme
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

val Context.dataStore by preferencesDataStore(name = "USER_PREFERENCES_NAME")

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    val cryptoManager = CryptoManager()
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

    /*
        fun createUserTable() {

        }*/
    @RequiresApi(Build.VERSION_CODES.M)
    fun saveUser(name: String, mail: String) {
        val bytes = mail + "#" + name.encodeToByteArray()
        val file = File(filesDir, "${CryptoManager.ALIAS}.txt")
        if (!file.exists()) {
            file.createNewFile()
        }
        val fos = FileOutputStream(file)
        cryptoManager.encrypt(mail.toByteArray(), fos).toString()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getUser():String {
        val file = File(filesDir, "$ALIAS.txt")
        return cryptoManager.decrypt(FileInputStream(file)).decodeToString()
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PoemBoxTheme {
        SetUpNavController(rememberNavController())
    }
}