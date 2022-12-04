package dev.pgm.poembox.roomUtils

import android.os.Build
import androidx.annotation.RequiresApi
import dev.pgm.poembox.MainActivity
import dev.pgm.poembox.MainActivity.Companion.USER_DATA


@RequiresApi(Build.VERSION_CODES.M)
class User(var userName: String?, var mail: String?) {
    @RequiresApi(Build.VERSION_CODES.M)
    fun existUserRegister(): Boolean {
       return USER_DATA.isNotEmpty()
    }

}

