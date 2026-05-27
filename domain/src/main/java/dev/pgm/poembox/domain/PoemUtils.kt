package dev.pgm.poembox.domain

import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject

class PoemUtils @Inject constructor(private val syllables: UtilitySyllables) {

    fun getNumberOfVerse(text: String): Int {
        var lines = 0
        text.forEach { if (it == '\n') lines++ }
        return lines + 1
    }

    fun getNumberStanza(text: String): Int {
        if (text.isBlank()) return 0
        var count = 0
        var inStanza = false
        for (line in text.split("\n")) {
            if (line.isNotBlank()) {
                if (!inStanza) { count++; inStanza = true }
            } else {
                inStanza = false
            }
        }
        return count
    }

    fun isProparoxytone(word: String): Int {
        val syllable = getTonicSyllable(word)
        if (syllable != null &&
            (syllable.contains('á') || syllable.contains('é') ||
             syllable.contains('í') || syllable.contains('ó') || syllable.contains('ú'))
        ) return 1
        return 0
    }

    private fun getTonicVowel(word: String): Int {
        var check = -1
        val letters = word.lowercase(Locale.getDefault()).toCharArray()
        val patternVowelsAccent = Pattern.compile("[áéíóú]")

        for (index in letters.indices) {
            val buf = StringBuffer().append(letters[index])
            if (patternVowelsAccent.matcher(buf).matches()) check = index
        }
        if (check != -1) return check

        val patternVowelsCaseNCaseS = Pattern.compile("[aeiouns]")
        val buf = StringBuffer().append(letters[letters.size - 1])
        if (patternVowelsCaseNCaseS.matcher(buf).matches()) {
            val vowels = word.indices.filter { syllables.isVowel(word[it]) }.toMutableList()
            if (vowels.size > 1) {
                vowels.removeAt(vowels.size - 1)
                check = vowels.last()
            }
        }
        return check
    }

    private fun getTonicSyllable(word: String): String? {
        val index = getTonicVowel(word)
        if (index != -1) {
            for (syllable in syllables.getSyllables(word)) {
                if (syllable.contains(word[index])) return syllable
            }
        }
        return null
    }

    fun isAcute(word: String): Int {
        val tonicSyllable = getTonicSyllable(word) ?: return 0
        val lastSyllable = syllables.getSyllables(word).lastOrNull() ?: return 0
        return if (tonicSyllable == lastSyllable) -1 else 0
    }

    fun hasSinhalese(line: String): Int {
        val words = line.split(" ").filter { it.isNotEmpty() }
        if (words.size < 2) return 0
        val firstLetters = words.drop(1).map { it.first() }
        val lastLetters = words.dropLast(1).map { it.last() }
        var count = 0
        for (i in firstLetters.indices) {
            if (syllables.isVowel(lastLetters[i]) &&
                (syllables.isVowel(firstLetters[i]) || firstLetters[i] == 'h')
            ) count++
        }
        return -count
    }

    fun getEnjambment(verse: String): String {
        // getSyllables strips punctuation before parsing, so we must check each original word
        // for punctuation BEFORE passing it to getSyllables, then accumulate syllable count.
        var cumulativeSyllables = 0
        for (word in verse.split(" ").filter { it.isNotEmpty() }) {
            cumulativeSyllables += syllables.getSyllables(word).size
            if (word.contains(",") || word.contains(".")) {
                return if (cumulativeSyllables < 5)
                    "Encabalgamiento abrupto: crea un ritmo sincopado, rápido e intenso."
                else
                    "Encabalgamiento suave: aporta gran fluidez al poema, que puede leerse " +
                    "casi como prosa poética. Otorga dinamismo y movimientos musicales ágiles."
            }
        }
        return "Sin encabalgamiento."
    }
}
