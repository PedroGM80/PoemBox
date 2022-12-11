package dev.pgm.poembox.repository

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Draft
 * @author Pedro Gallego Morales
 * @constructor Create empty Draft
 * @property title
 * @property draftContent
 * @property writerName
 * @property draftAnnotation
 * @property writtenDate
 */
@Entity(
    tableName = "drafts", indices = [Index(
        value = ["title"],
        unique = true
    )]
)
data class Draft(

    @ColumnInfo(name = "title")

    val title: String,
    @ColumnInfo(name = "draftContent") val draftContent: String,
    @ColumnInfo(name = "writerName") val writerName: String,
    @ColumnInfo(name = "draftAnnotation") var draftAnnotation: String = "",
    @ColumnInfo(name = "writtenDate") val writtenDate: String = ""

) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")
    var id: Int = 0

    @ColumnInfo(name = "dateValidation")
    var dateValidation: String? = ""

}