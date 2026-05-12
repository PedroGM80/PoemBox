package dev.pgm.poembox.domain.model

data class Draft(
    val id: Int = 0,
    val title: String,
    val content: String,
    val author: String,
    val annotation: String = "",
    val date: String = "",
    val validationDate: String? = null
)
