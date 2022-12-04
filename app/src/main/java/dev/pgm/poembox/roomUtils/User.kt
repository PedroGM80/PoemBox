package dev.pgm.poembox.roomUtils

import android.os.Build
import androidx.annotation.RequiresApi
import dev.pgm.poembox.MainActivity


@RequiresApi(Build.VERSION_CODES.M)
class User(var userName: String?, var mail: String?) {
    @RequiresApi(Build.VERSION_CODES.M)
    fun existUserRegister(): Boolean {
        return MainActivity().getUser().isNotEmpty()
    }

}

