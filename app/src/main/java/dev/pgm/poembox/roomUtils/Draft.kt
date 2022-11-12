package dev.pgm.poembox.roomUtils

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "drafts")
data class Draft(
    @PrimaryKey
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "draftContent") val draftContent: String?,
    @ColumnInfo(name = "draftAnnotation") val draftAnnotation: String?,
    @ColumnInfo(name = "writerName") val writerName: String,
    @ColumnInfo(name = "writtenDate") val writtenDate: Date,
    @ColumnInfo(name = "dateValidation") val dateValidation: Date?
)