package dev.pgm.poembox.domain.model

import androidx.annotation.StringRes
import dev.pgm.poembox.R

data class PoeticFormDef(
    val id: String,
    @StringRes val nameRes: Int,
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
    val expectedSyllables: Int?,  // null = free
    val rhymeLetter: Char?,       // null = no scheme for this line
    val syllableOk: Boolean       // true if free or matches expected
)

object PoeticForms {
    val LIBRE = PoeticFormDef(
        id = "libre",
        nameRes = R.string.form_libre
    )

    val HAIKU = PoeticFormDef(
        id = "haiku",
        nameRes = R.string.form_haiku,
        totalLines = 3,
        syllablesPerLine = listOf(5, 7, 5)
    )

    // Redondilla: 4 octosílabos, rima ABBA
    val REDONDILLA = PoeticFormDef(
        id = "redondilla",
        nameRes = R.string.form_redondilla,
        totalLines = 4,
        syllablesPerLine = listOf(8, 8, 8, 8),
        rhymeScheme = listOf('a', 'b', 'b', 'a')
    )

    // Cuarteta: 4 octosílabos, rima ABAB
    val CUARTETA = PoeticFormDef(
        id = "cuarteta",
        nameRes = R.string.form_cuarteta,
        totalLines = 4,
        syllablesPerLine = listOf(8, 8, 8, 8),
        rhymeScheme = listOf('a', 'b', 'a', 'b')
    )

    // Cuarteto: 4 endecasílabos, rima ABBA
    val CUARTETO = PoeticFormDef(
        id = "cuarteto",
        nameRes = R.string.form_cuarteto,
        totalLines = 4,
        syllablesPerLine = listOf(11, 11, 11, 11),
        rhymeScheme = listOf('a', 'b', 'b', 'a')
    )

    // Soneto petrarquista: 14 endecasílabos, ABBA ABBA CDC DCD
    val SONETO = PoeticFormDef(
        id = "soneto",
        nameRes = R.string.form_soneto,
        totalLines = 14,
        syllablesPerLine = List(14) { 11 },
        rhymeScheme = "abbaabbacdcdcd".map { it }
    )

    // Décima espinela: 10 octosílabos, abbaaccddc
    val DECIMA = PoeticFormDef(
        id = "decima",
        nameRes = R.string.form_decima,
        totalLines = 10,
        syllablesPerLine = List(10) { 8 },
        rhymeScheme = "abbaaccddc".map { it }
    )

    val ALL = listOf(LIBRE, HAIKU, REDONDILLA, CUARTETA, CUARTETO, SONETO, DECIMA)
}
