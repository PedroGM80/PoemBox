package dev.pgm.poembox

data class PoemDraft(
    val title: String,
    val draftContent: String,
    val draftAnnotation: String,
    val writer: User
)
