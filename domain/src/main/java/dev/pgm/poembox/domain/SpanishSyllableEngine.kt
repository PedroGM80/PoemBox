package dev.pgm.poembox.domain

import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject

class SpanishSyllableEngine @Inject constructor() : SyllableEngine {
    private val conversions = arrayOf(
        arrayOf("ch", "@"),
        arrayOf("ll", "#"),
        arrayOf("gue", "%e"),
        arrayOf("gué", "%é"),
        arrayOf("gui", "%i"),
        arrayOf("guí", "%í"),
        arrayOf("qu", "&"),
        arrayOf("rr", "$"),
        arrayOf("ya", "|a"),
        arrayOf("ye", "|e"),
        arrayOf("yi", "|i"),
        arrayOf("yo", "|o"),
        arrayOf("yu", "|u")
    )
    private val openVowels = charArrayOf('a', 'á', 'e', 'é', 'o', 'ó')
    private val closeVowels = charArrayOf('i', 'u', 'ü', 'y')
    private val closeVowelsAccent = charArrayOf('í', 'ú')
    private val patternAccent: Pattern = Pattern.compile(".*([áéíóú]).*")
    private val patternVowelsCaseNCaseS: Pattern = Pattern.compile(".*([áéíóúaeiouns])")
    private val vowels: CharArray = openVowels + closeVowels + closeVowelsAccent

    override fun getSyllables(word: String): List<String> {
        val wordNoPoint = word.replace(".", "").replace(",", "")
        var formattedWord = format(wordNoPoint)
        val syllables = mutableListOf<String>()
        
        while (formattedWord.isNotEmpty()) {
            val cut = next(formattedWord) + 1
            val syllable = unFormat(formattedWord.substring(0, cut))
            formattedWord = formattedWord.substring(cut)
            syllables.add(syllable)
        }
        return syllables
    }

    override fun getStressedSyllableIndex(syllables: List<String>): Int {
        if (syllables.size <= 1) return 0

        for (index in syllables.indices) {
            if (patternAccent.matcher(syllables[index]).matches()) return index
        }
        
        val last = syllables.last()
        return if (patternVowelsCaseNCaseS.matcher(last).matches()) {
            syllables.size - 2 // Grave/Llana
        } else {
            syllables.size - 1 // Aguda
        }
    }

    override fun isVowel(letter: Char): Boolean {
        for (vowel in vowels) {
            if (letter.lowercaseChar() == vowel) return true
        }
        return false
    }

    private fun next(word: String): Int {
        val charsWord = word.toCharArray()
        if (charsWord.size >= 4 && charsWord[0] == 's' && charsWord[1] == 'u' && charsWord[2] == 'b' && charsWord[3] == 'r') return 2
        
        var vowelPos = 0
        var found = false
        while (vowelPos < charsWord.size && !found) {
            found = isVowel(charsWord[vowelPos])
            if (!found) vowelPos++
        }
        
        if (lastVowel(vowelPos, charsWord)) return word.length - 1
        
        var lastLetter = vowelPos + 1
        var hInterleaved = 0
        if (charsWord[lastLetter] == 'h') {
            lastLetter++
            hInterleaved = 1
        }

        if (lastLetter + 1 == charsWord.size) {
            return if (isVowel(charsWord[lastLetter]) && isHiatus(charsWord[vowelPos], charsWord[lastLetter])) {
                vowelPos
            } else {
                lastLetter + hInterleaved
            }
        }

        var lastLetterSecondCase = lastLetter + 1
        if (charsWord[lastLetterSecondCase] == 'h') {
            lastLetterSecondCase++
            hInterleaved = 1
        }

        if (isConsonant(charsWord[lastLetter]) && isVowel(charsWord[lastLetterSecondCase])) {
            return vowelPos
        } else if (isConsonant(charsWord[lastLetter]) && isConsonant(charsWord[lastLetterSecondCase])) {
            val groupDoubleConsonant = arrayOf("tr", "gr", "pr", "br", "bl", "fr", "fl", "cl", "dr", "pl")
            val token = "${charsWord[lastLetter]}${charsWord[lastLetterSecondCase]}".lowercase()
            if (token in groupDoubleConsonant) return vowelPos
            if (token == "ns" && charsWord.size > lastLetterSecondCase + 1 && isConsonant(charsWord[lastLetterSecondCase + 1])) {
                return lastLetterSecondCase
            }
            return lastLetter + hInterleaved
        } else if (isVowel(charsWord[lastLetter])) {
            return if (isHiatus(charsWord[vowelPos], charsWord[lastLetter])) {
                vowelPos + hInterleaved
            } else {
                vowelPos + next(word.substring(lastLetter)) + 1 + hInterleaved
            }
        }
        return 0
    }

    private fun lastVowel(vocal: Int, aWord: CharArray): Boolean {
        for (i in vocal + 1 until aWord.size) {
            if (isVowel(aWord[i])) return false
        }
        return true
    }

    private fun isHiatus(firstVowel: Char, secondVowel: Char): Boolean {
        for (v in closeVowelsAccent) if (v == firstVowel || v == secondVowel) return true
        if (firstVowel in openVowels && secondVowel in openVowels) return true
        return firstVowel == secondVowel
    }

    private fun isConsonant(letter: Char): Boolean = !isVowel(letter)

    private fun format(aWord: String): String {
        var word = aWord
        for (conv in conversions) word = word.replace(conv[0], conv[1])
        if (word.startsWith("cacah")) word = word.replace("h", "¬")
        return word
    }

    private fun unFormat(aWord: String): String {
        var word = aWord
        for (conv in conversions) word = word.replace(conv[1], conv[0])
        return word.replace("¬", "h")
    }
}
