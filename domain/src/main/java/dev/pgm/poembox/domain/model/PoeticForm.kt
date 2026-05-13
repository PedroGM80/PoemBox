package dev.pgm.poembox.domain.model

data class PoeticFormDef(
    val id: String,
    val nameRes: Int,                     // Resource ID from app module
    val totalLines: Int = 0,                     // 0 = unlimited
    val syllablesPerLine: List<Int> = emptyList(), // empty = free, otherwise per-line expected count
    val rhymeScheme: List<Char> = emptyList()      // empty = no scheme
) {
    fun getSyllablesForLine(index: Int): Int? = syllablesPerLine.getOrNull(index)
    fun getRhymeLetterForLine(index: Int): Char? = rhymeScheme.getOrNull(index)
    val isFree: Boolean get() = syllablesPerLine.isEmpty() && rhymeScheme.isEmpty() && totalLines == 0
}

data class LineValidation(
    val index: Int,
    val lineText: String,
    val actualSyllables: Int,
    val expectedSyllables: Int?,   // null = free
    val rhymeLetter: Char?,        // null = no scheme
    val syllableOk: Boolean,       // true if free or matches expected
    val rhymesOk: Boolean? = null, // null = pending (first line with letter), true/false = result
    val rhymeHint: String? = null  // e.g. "-or" — ending the line should match
)
