package dev.pgm.poembox.roomUtils

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "drafts",indices = [Index(value = ["title"],
    unique = true)])
data class Draft(

    @ColumnInfo(name = "title")

    val title: String,
    @ColumnInfo(name = "draftContent") val draftContent: String,
    @ColumnInfo(name = "writerName") val writerName: String,
    @ColumnInfo(name = "draftAnnotation") val draftAnnotation: String = "",
    @ColumnInfo(name = "writtenDate") val writtenDate: String = ""

) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id") var id: Int=0
    @ColumnInfo(name = "dateValidation")
    var dateValidation: String? = ""

}