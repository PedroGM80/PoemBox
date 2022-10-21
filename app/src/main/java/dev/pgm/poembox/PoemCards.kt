package dev.pgm.poembox

import java.util.Date

data class PoemCard(
    val id: Int,
    val descriptiveName: String,
    val createdDate: Date,
    val validateDraftRef:String
)
