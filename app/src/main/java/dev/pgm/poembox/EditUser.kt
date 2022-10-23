package dev.pgm.poembox

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class EditUser(val poemEdited: PoemDraft, userName: String, Id: Int, mail: String) :
    User(userName, mail) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun submitPoem(title: String, draftContent: List<String>) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val current = LocalDateTime.now().format(formatter)
        /*val poemEdited = PoemDraft(
            1, title,
            draftContent,
            current,
            null
        )*/

    }

}
