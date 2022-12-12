package dev.pgm.poembox.presentation

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.navigation.compose.rememberNavController
import dev.pgm.poembox.domain.ContextContentProvider
import dev.pgm.poembox.domain.CryptoManager
import dev.pgm.poembox.domain.CryptoManager.Companion.ALIAS
import dev.pgm.poembox.domain.ShowBars
import dev.pgm.poembox.presentation.screens.SetUpNavController
import dev.pgm.poembox.presentation.theme.PoemBoxTheme
import dev.pgm.poembox.repository.User
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * Main activity
 * @author Pedro Gallego Morales
 * @constructor Create empty Main activity
 */
class MainActivity : ComponentActivity() {
    companion object {
        internal var POEM_TITLE = ""
        internal var VALIDATE_STATUS = 0
    }
   private var userData = ""
    internal var user = User(null, null)

    @RequiresApi(Build.VERSION_CODES.M)
    internal val cryptoManager = CryptoManager()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var userData = getDecode()

        setContent {
            PoemBoxTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    //set up nav controller
                    ShowBars(flag = false)
                    SetUpNavController(rememberNavController(), userData)
                }
            }
        }
    }


    /**
     * Save user
     *
     * @param user
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun saveUser(user: User) {
        userData=user.userMail.toString()+"#"+user.userName.toString()
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

    /**
     * Get decode
     *
     * @return Decode info
     */
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