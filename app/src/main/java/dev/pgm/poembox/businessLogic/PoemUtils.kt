package dev.pgm.poembox.businessLogic

import java.util.*
import java.util.regex.Pattern

class PoemUtils {
    fun countLinesWrote(text: String): Int {// This function return the number of lines wrote in the string
        var lines = 0
        text.forEach { element ->
            if (element == '\n') {
                lines++
            }
        }
        return lines
    }

    fun countEmptyLines(text: String): Int {// This function return the number of empty lines in the text field
        var emptyLines = 0
        text.forEach { element ->
            if (element == '\n') {
                if (text.substring(text.indexOf(element) + 1, text.indexOf(element) + 2) == " ") {
                    emptyLines++
                }
            }
        }
        return emptyLines
    }

    fun countWords(text: String): Int {// This function return the number of words wrote in the text field
        var words = 0
        text.forEach { element ->
            if (element == ' ') {
                words++
            }
        }
        return words
    }

    fun countCharacters(text: String): Int {// This function return the number of characters wrote in the text field
        var characters = 0
        text.forEach { _ -> characters++ }
        return characters
    }

    fun isProparoxytone(word: String): Int {
        val syllables = UtilitySyllables().getSyllables(word)
        val syllablesB = syllables.removeLast()
        val syllable = syllablesB.last()
        val stringBuffer = StringBuffer()
        stringBuffer.append(syllable)
        return if (UtilitySyllables().patternAccent.matcher(stringBuffer).matches()) {
            1
        } else {
            0
        }
    }

    private fun getTonicVowel(word: String): Int {
        var check = -1
        val letters = word.lowercase(Locale.getDefault()).toCharArray()
        val patternVowelsAccent = Pattern.compile("[áéíóú]")

        for (index in letters.indices) {
            val stringBuffer = StringBuffer()
            stringBuffer.append(letters[index])
            if (patternVowelsAccent.matcher(stringBuffer).matches()) {
                println("aqui")
                check = index
            }
        }
        if (check != -1) return check
        val patternVowelsCaseNCaseS: Pattern = Pattern.compile("[aeiouns]")
        val stringBuffer = StringBuffer()
        stringBuffer.append(letters[letters.size - 1])
        val vowels = mutableListOf<Int>()
        if (patternVowelsCaseNCaseS.matcher(stringBuffer).matches()) {
            for (index in word.indices) {
                if (UtilitySyllables().isVowel(word[index])) {
                    vowels.add(index)
                }
            }
            vowels.removeLast()
            check = vowels.last()
        }
        return check
    }

    fun getTonicSyllable(word: String): String? {
        val index = getTonicVowel(word)
        if (index != -1) {
            val syllables = UtilitySyllables().getSyllables(word)
            for (syllable in syllables) {
                if (syllable.contains(word[index])) {
                    return syllable
                }
            }
        }
        return null
    }

    fun isAcute(word: String): Int {
        val tonicSyllable = getTonicSyllable(word)
        return if (tonicSyllable != null) {
            val lastSyllable = UtilitySyllables().getSyllables(word).last()
            if (tonicSyllable == lastSyllable) {
                -1
            } else {
                0
            }
        } else {
            0
        }
    }

    fun hasSinhalese(line: String): Int {
        val firstsLetters = mutableListOf<Char>()
        val lastLetters = mutableListOf<Char>()
        var result = false
        var sinhaleseCount = 0
        val words = line.split(" ")
        for (index in words.indices) {
            val word = words[index]
            firstsLetters.add(word.toCharArray().first())
            lastLetters.add(word.toCharArray().last())
        }
        firstsLetters.removeFirst()
        lastLetters.removeLast()
        val utilitySyllables = UtilitySyllables()
        for (index in firstsLetters.indices) {
            println("ultima" + lastLetters[index] + "primera " + firstsLetters[index])
            if (utilitySyllables.isVowel(firstsLetters[index]) && utilitySyllables.isVowel(
                    lastLetters[index]
                )
            ) {
                sinhaleseCount++
            }
            if (firstsLetters[index] == 'h' && utilitySyllables.isVowel(lastLetters[index])) {
                sinhaleseCount++
            }
        }
        return -sinhaleseCount
    }

}

fun main() {
    val number = PoemUtils().hasSinhalese("que solo toca en ellas, manso, el viento")
    println(number)
    val numberS = UtilitySyllables().getSyllables("que solo toca en ellas, manso, el viento")
    println(numberS.size)
}