package dev.pgm.poembox.businessLogic

import java.util.*
import java.util.regex.Pattern

class UtilitySyllables {
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
    private val punctuationMarks = charArrayOf(',', '.')
    private val openVowels = charArrayOf('a', 'á', 'e', 'é', 'o', 'ó')
    private val closeVowels = charArrayOf('i', 'u', 'ü', 'y')
    private val closeVowelsAccent = charArrayOf('í', 'ú')
    val patternAccent: Pattern = Pattern.compile(".*([áéíóú]).*")
    private val patternVowelsCaseNCaseS: Pattern = Pattern.compile(".*([áéíóúaeiouns])")
    private val enye = 'ñ'
    private val vowels: CharArray
        get() {
            val size = openVowels.size + closeVowels.size + closeVowelsAccent.size
            val vowels = CharArray(size)
            var commonIndex = 0
            for (indexOpenVowels in openVowels.indices) {
                vowels[commonIndex] = openVowels[indexOpenVowels]
                commonIndex++
            }
            for (indexCloseVowels in closeVowels.indices) {
                vowels[commonIndex] = closeVowels[indexCloseVowels]
                commonIndex++
            }
            for (indexCloseVowelsAccent in closeVowelsAccent.indices) {
                vowels[commonIndex] = closeVowelsAccent[indexCloseVowelsAccent]
                commonIndex++
            }
            return vowels
        }
    private val otherCase: CharArray
        get() {
            val size = vowels.size + 1
            val otherCase = CharArray(size)
            for (index in vowels.indices) {
                otherCase[index] = openVowels[index]
            }
            otherCase[size] = enye
            return otherCase
        }
    val pattern: Pattern
        get() {
            val regex = StringBuffer(otherCase[0].code)
            for (index in 1 until otherCase.size) {
                regex.append("|").append(otherCase[index])
            }
            return Pattern.compile(regex.toString())
        }


    fun getSyllables(aWord: String?): ArrayList<String> {
        val wordNoPoint = aWord?.replace(".", "")
        val wordNoPoints = wordNoPoint?.replace(",", "")
        var word = wordNoPoints

        word = format(word)
        var cut: Int
        var syllable: String
        val syllables = ArrayList<String>()
        while (word!!.isNotEmpty()) {
            cut = next(word) + 1
            syllable = unFormat(word.substring(0, cut))
            word = word.substring(cut)
            syllables.add(syllable)
        }
        return syllables
    }

    fun next(word: String?): Int {
        var hInterleaved = 0
        val charsWord = word!!.toCharArray()

        if (charsWord[0] == 's' && charsWord[1] == 'u' && charsWord[2] == 'b' && charsWord[3] == 'r') return 2
        var vowel = 0
        var found = false
        while (vowel < charsWord.size && !found) {
            found = isVowel(charsWord[vowel])
            if (!found) vowel++
        }
        if (lastVowel(vowel, charsWord)) return word.length - 1
        var lastLetter = vowel + 1
        if (charsWord[lastLetter] == 'h') {

            lastLetter++
            hInterleaved = 1
        }

        if (lastLetter + 1 == charsWord.size) {

            return if (isVowel(charsWord[lastLetter]) && isHiatus(
                    charsWord[vowel],
                    charsWord[lastLetter]
                )
            ) {
                vowel
            } else {

                lastLetter + hInterleaved
            }
        }
        var lastLetterSecondCase = lastLetter + 1
        if (charsWord[lastLetterSecondCase] == 'h') {

            lastLetterSecondCase++
            hInterleaved = 1
        }
        if (isConsonant(charsWord[lastLetter]) && isVowel(charsWord[lastLetterSecondCase])) {// VCV
            return vowel
        } else if (isConsonant(charsWord[lastLetter]) && isConsonant(charsWord[lastLetterSecondCase])) // VCC
        {
            val groupDoubleConsonant =
                arrayOf("tr", "gr", "pr", "br", "bl", "fr", "fl", "cl", "dr", "pl")
            val tokenChar = charArrayOf(charsWord[lastLetter], charsWord[lastLetterSecondCase])
            val token = String(tokenChar).lowercase(Locale.getDefault())
            for (aString in groupDoubleConsonant) {
                if (aString == token) return vowel
            }
            if ("ns" == token) {
                if (charsWord.size > lastLetterSecondCase + 1 && isConsonant(charsWord[lastLetterSecondCase + 1])) {
                    return lastLetterSecondCase // case ns
                }
            }
            return lastLetter + hInterleaved
        } else if (isVowel(charsWord[lastLetter])) {
            return if (isHiatus(
                    charsWord[vowel],
                    charsWord[lastLetter]
                )
            ) vowel + hInterleaved else vowel + next(word.substring(lastLetter)) + 1 + hInterleaved
        }
        return 0
    }

    private fun lastVowel(vocal: Int, aWord: CharArray): Boolean {
        for (i in vocal + 1 until aWord.size) {
            if (isVowel(aWord[i])) {
                return false
            }
        }
        return true
    }

    private fun isHiatus(firstVowel: Char, secondVowel: Char): Boolean {
        // one closeVowels and accent
        for (closeVowel in closeVowelsAccent) {
            if (closeVowel == firstVowel || closeVowel == secondVowel) return true
        }
        // two openVowels
        for (openVowel in openVowels) {
            if (openVowel == firstVowel) {
                for (aOpenVowel in openVowels) {
                    if (aOpenVowel == secondVowel) return true
                }
            }
        }
        return firstVowel == secondVowel
    }

    fun isVowel(letter: Char): Boolean {
        for (vowel in vowels) {
            if (letter.lowercaseChar() == vowel) return true
        }
        return false
    }

    private fun isConsonant(letter: Char): Boolean = !isVowel(letter)


    private fun format(aWord: String?): String {
        var word = aWord
        if (word == null) word = ""
        for (i in conversions.indices) {
            word = word!!.replace(conversions[i][0], conversions[i][1])
        }
        // case h interleaved
        if (word!!.startsWith("cacah")) word = word.replace("h", "¬")
        return word
    }

    private fun unFormat(aWord: String?): String {
        var word = aWord
        if (word == null) word = ""
        for (index in conversions.indices) {

            word = word!!.replace(conversions[index][1], conversions[index][0])
        }
        word = word!!.replace("¬", "h")
        return word
    }


    fun stressed(syllables: List<String?>): Int {
        if (syllables.size == 1) return 0

        for (index in syllables.indices) {
            if (patternAccent.matcher(syllables[index].toString())
                    .matches()
            ) return index//have accent is tonic
        }
        val last = syllables[syllables.size - 1]
        return when {
            patternVowelsCaseNCaseS.matcher(last.toString())
                .matches() -> syllables.size - 2 // plain
            else -> syllables.size - 1//acute
        }
    }

    fun stressedB(syllables: String): Int {
        if (syllables.length == 1) return 0

        for (index in syllables.indices) {
            if (patternAccent.matcher(syllables[index].toString())
                    .matches()
            ) return index//have accent is tonic
        }
        val last = syllables[syllables.length - 1]
        return when {
            patternVowelsCaseNCaseS.matcher(last.toString())
                .matches() -> syllables.length - 2 // plain
            else -> syllables.length - 1//acute
        }
    }

    fun getStressedVowelIndex(syllable: String): Int {
        val letters = syllable.lowercase(Locale.getDefault()).toCharArray()
        var check = -1
        var onlyOneVowel = false
        val patternVowels = Pattern.compile("[aeiouáéíóú]")
        val patternVowelsAccent = Pattern.compile("[áéíóú]")
        val patternOpenVowels = Pattern.compile("[aáeéoó]")


        for (index in letters.indices) {
            val stringBuffer = StringBuffer()
            stringBuffer.append(letters[index])
            if (patternVowels.matcher(stringBuffer).matches()) {
                onlyOneVowel = true
                check = index
            }
        }
        if (onlyOneVowel) return check

        // if this has interleaved
        check = -1
        for (index in letters.indices) {
            val stringBuffer = StringBuffer()
            stringBuffer.append(letters[index])
            if (patternVowelsAccent.matcher(stringBuffer).matches()) {
                check = index
            }
        }
        if (check != -1) return check

        check = -1
        for (index in letters.indices) {
            val stringBuffer = StringBuffer()
            stringBuffer.append(letters[index])
            if (patternOpenVowels.matcher(stringBuffer).matches()) {
                check = index
            }
        }
        return check
    }


    fun getLastSyllable(word: String): String {
        return getSyllables(word).last()
    }

    fun getLastVowel(word: String): String {
        var vowel = ""
        for (letter in word) {
            if (isVowel(letter)) {
                vowel = letter.toString()
            }
        }
        return vowel
    }




}