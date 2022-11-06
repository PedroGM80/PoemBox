package dev.pgm.poembox.roomUtils

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.pgm.poembox.User

@Entity
data class PoemDraft(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val title: String,
    val draftContent: String,
    val draftAnnotation: String,
    val writer: User,
    val date:String
)
