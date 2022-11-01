package dev.pgm.poembox

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity
data class EditUser(
    val poemEdited: PoemDraft,
    val user: User,
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun submitPoem(id:Int,title: String, draftContent: List<String>) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val current = LocalDateTime.now().format(formatter)
        val poemEdited = PoemDraft(
            id=id,
            title = title,
            draftContent = draftContent.toString(),
            draftAnnotation = "test",
            writer = user,
            date = current
        )
    }
}
