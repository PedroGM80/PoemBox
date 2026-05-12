package dev.pgm.poembox.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "drafts",
    indices = [Index(value = ["title"], unique = true)]
)
data class DraftEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")
    val id: Int = 0,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "draftContent")
    val content: String,
    @ColumnInfo(name = "writerName")
    val author: String,
    @ColumnInfo(name = "draftAnnotation")
    val annotation: String = "",
    @ColumnInfo(name = "writtenDate")
    val date: String = "",
    @ColumnInfo(name = "dateValidation")
    val validationDate: String? = ""
)
