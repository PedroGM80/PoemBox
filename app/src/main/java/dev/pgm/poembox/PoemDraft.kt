package dev.pgm.poembox

data class PoemDraft(
    val title: String,
    val draftContent: List<String>,
    val draftAnnotation: String,
    val writer: User
)
