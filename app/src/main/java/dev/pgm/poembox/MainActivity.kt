package dev.pgm.poembox

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.navigation.compose.rememberNavController
import dev.pgm.poembox.CryptoManager.Companion.ALIAS
import dev.pgm.poembox.roomUtils.User
import dev.pgm.poembox.screens.SetUpNavController
import dev.pgm.poembox.ui.theme.PoemBoxTheme
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    var user = User(null, null)



    @RequiresApi(Build.VERSION_CODES.M)
    val cryptoManager = CryptoManager()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       val userData = getDecode()

        setContent {
            PoemBoxTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    //set up nav controller
                    ShowBars(flag = false)
                    SetUpNavController(rememberNavController(),userData)
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun saveUser(user: User) {
        val name: String = user.userName ?: ""
        val mail: String = user.userMail ?: ""
        val info = "$mail#$name"
        val fileDir = ContextContentProvider.applicationContext()!!.filesDir
        if (File(fileDir, "$ALIAS.txt").exists()) {

            val file = File(fileDir, "$ALIAS.txt")
            val fileOutputStream = FileOutputStream(file)
            cryptoManager.encrypt(info.toByteArray(), fileOutputStream).toString()
        } else {

            File(filesDir, "$ALIAS.txt").createNewFile()
            val file = File(filesDir, "$ALIAS.txt")
            val fileOutputStream = FileOutputStream(file)
            cryptoManager.encrypt(info.toByteArray(), fileOutputStream).toString()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getDecode(): String {
        return if (File(filesDir, "$ALIAS.txt").exists()) {
            val file = File(filesDir, "$ALIAS.txt")
            Log.i(":::ISTREAM", FileInputStream(file).toString())
            cryptoManager.decrypt(FileInputStream(file)).decodeToString()
        } else {
            File(filesDir, "$ALIAS.txt").createNewFile()
            val file = File(filesDir, "$ALIAS.txt")
            Log.i(":::ISTREAM", FileInputStream(file).toString())
            cryptoManager.decrypt(FileInputStream(file)).decodeToString()
        }
    }
}


/*
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PoemBoxTheme {
        SetUpNavController(rememberNavController())
    }
}*/
