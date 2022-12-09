package dev.pgm.poembox.roomUtils

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.pgm.poembox.content.getDate

@Entity(tableName = "sheets")
data class Sheet(
    @ColumnInfo(name = "RefDraftValidate")
    val refDraftValidate:String){
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id") var id: Int=0
    @ColumnInfo(name = "dateCreation")
    var dateValidation: String? = getDate()
}
