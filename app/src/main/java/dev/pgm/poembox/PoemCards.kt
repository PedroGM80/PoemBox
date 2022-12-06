package dev.pgm.poembox

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class PoemCard(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val descriptiveName: String,
    val createdDate: Date,
    val validateDraftRef: String
)
