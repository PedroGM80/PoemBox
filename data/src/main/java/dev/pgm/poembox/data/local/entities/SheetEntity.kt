package dev.pgm.poembox.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sheets")
data class SheetEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")
    val id: Int = 0,
    @ColumnInfo(name = "RefDraftValidate")
    val draftTitle: String,
    @ColumnInfo(name = "dateCreation")
    val validationDate: String
)
