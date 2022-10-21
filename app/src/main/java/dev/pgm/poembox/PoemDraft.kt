package dev.pgm.poembox

import java.util.Date

data class PoemDraft(
    val id: Int,
    val title: String,
    val draftContent: List<String>,
    val editDate: Date,
    val validDate: Date?
)
