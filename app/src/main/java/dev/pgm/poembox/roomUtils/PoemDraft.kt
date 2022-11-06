package dev.pgm.poembox.roomUtils

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.pgm.poembox.User
import java.util.Date

@Entity
data class PoemDraft(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id") val id:Int,
    @ColumnInfo(name = "title")val title: String,
    @ColumnInfo(name = "draftContent")val draftContent: String,
    @ColumnInfo(name = "draftAnnotation")val draftAnnotation: String,
    @ColumnInfo(name = "writerName")val writerName:String,
    @ColumnInfo(name = "WrittenDate")val WrittenDate:String?="CURRENT_TIMESTAMP",
    @ColumnInfo(name = "dateValidation")val dateValidation:String?=null
)
