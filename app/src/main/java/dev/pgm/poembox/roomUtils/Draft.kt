package dev.pgm.poembox.roomUtils

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "drafts")
data class Draft(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id") var id: Int = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "draftContent") val draftContent: String,
    @ColumnInfo(name = "writerName") val writerName: String,
    @ColumnInfo(name = "draftAnnotation") val draftAnnotation: String = "",
    @ColumnInfo(name = "writtenDate") val writtenDate: String = ""

    ) {
    @ColumnInfo(name = "dateValidation") var dateValidation: String? = getDate()
    private fun getDate(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:MM:SS")
        val date = Date()
        return formatter.format(date).toString()
    }
}