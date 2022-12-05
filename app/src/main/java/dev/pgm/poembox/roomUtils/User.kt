package dev.pgm.poembox.roomUtils

import android.os.Build
import androidx.annotation.RequiresApi
import dev.pgm.poembox.MainActivity.Companion.USER_DATA



class User(var userName: String?, var userMail: String?) {

    @RequiresApi(Build.VERSION_CODES.M)
    fun existUserRegister(): Boolean {
       return USER_DATA.isNotEmpty()
    }

}

