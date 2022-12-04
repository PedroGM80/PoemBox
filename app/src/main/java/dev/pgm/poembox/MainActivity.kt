package dev.pgm.poembox

import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import dev.pgm.poembox.CryptoManager.Companion.ALIAS
import dev.pgm.poembox.roomUtils.User
import dev.pgm.poembox.screens.SetUpNavController
import dev.pgm.poembox.ui.theme.PoemBoxTheme
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    companion object{
        var USER_DATA = ""
    }
    @RequiresApi(Build.VERSION_CODES.M)
    val cryptoManager = CryptoManager()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        USER_DATA = getUser()
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


    @RequiresApi(Build.VERSION_CODES.M)
    fun saveUser(user: User) {
        val name:String = user.userName?:""
        val mail:String = user.mail?:""
        val bytes = mail + "#" + name.encodeToByteArray()
        val file = File(filesDir, "$ALIAS.txt")
        if (!file.exists()) {
            file.createNewFile()
        }
        val fileOutputStream = FileOutputStream(file)
        cryptoManager.encrypt(bytes.toByteArray(), fileOutputStream).toString()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getUser(): String {
        val newFile = File(filesDir, "$ALIAS.txt").createNewFile()
        val file = File(filesDir, "$ALIAS.txt")
        Log.i(":::ISTREAM",FileInputStream(file).toString())
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