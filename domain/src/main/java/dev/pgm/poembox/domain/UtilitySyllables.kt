package dev.pgm.poembox.domain

import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject

class UtilitySyllables @Inject constructor() {
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
    val patternAccent: Pattern = Pattern.compile(".*([áéíóú]).*")
    private val patternVowelsCaseNCaseS: Pattern = Pattern.compile(".*([áéíóúaeiouns])")
    private val enye = 'ñ'
    // Optimized: vowels is pre-initialized once to avoid allocating/copying arrays inside tight loops!
    private val vowels: CharArray = openVowels + closeVowels + closeVowelsAccent

    // Optimized: Precompiled regex patterns for getStressedVowelIndex to avoid rebuilding them on every call!
    private val patternVowels = Pattern.compile("[aeiouáéíóú]")
    private val patternVowelsAccent = Pattern.compile("[áéíóú]")
    private val patternOpenVowels = Pattern.compile("[aáeéoó]")


    /**
     * Get syllables
     *
     * @param aWord
     * @return syllables list
     */
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

    /**
     * Next
     *
     * @param word
     * @return Int
     */
    fun next(word: String?): Int {
        var hInterleaved = 0
        val charsWord = word!!.toCharArray()

        if (charsWord.size >= 4 && charsWord[0] == 's' && charsWord[1] == 'u' && charsWord[2] == 'b' && charsWord[3] == 'r') return 2
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

    /**
     * Last vowel
     *
     * @param vocal
     * @param aWord
     * @return boolean
     */
    private fun lastVowel(vocal: Int, aWord: CharArray): Boolean {
        for (i in vocal + 1 until aWord.size) {
            if (isVowel(aWord[i])) {
                return false
            }
        }
        return true
    }

    /**
     * Is hiatus
     *
     * @param firstVowel
     * @param secondVowel
     * @return boolean
     */
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

    /**
     * Is vowel
     *
     * @param letter
     * @return
     */
    fun isVowel(letter: Char): Boolean {
        for (vowel in vowels) {
            if (letter.lowercaseChar() == vowel) return true
        }
        return false
    }

    /**
     * Is consonant
     *
     * @param letter
     * @return boolean
     */
    private fun isConsonant(letter: Char): Boolean = !isVowel(letter)


    /**
     * Format
     *
     * @param aWord
     * @return word  formatted
     */
    private fun format(aWord: String?): String {
        var word = aWord
        if (word == null) word = ""
        for (i in conversions.indices) {
            word = word!!.replace(conversions[i][0], conversions[i][1])
        }
        // case h interleaved
        if (word.startsWith("cacah")) word = word.replace("h", "¬")
        return word
    }

    /**
     * Un format
     *
     * @param aWord
     * @return word reverted format
     */
    private fun unFormat(aWord: String?): String {
        var word = aWord
        if (word == null) word = ""
        for (index in conversions.indices) {

            word = word!!.replace(conversions[index][1], conversions[index][0])
        }
        word = word.replace("¬", "h")
        return word
    }


    /**
     * Stressed
     *
     * @param syllables
     * @return index stressed syllable
     */
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

    /**
     * Get stressed vowel index
     *
     * @param syllable
     * @return  index stressed syllable
     */
    fun getStressedVowelIndex(syllable: String): Int {
        val letters = syllable.lowercase(Locale.getDefault()).toCharArray()
        var check = -1
        var onlyOneVowel = false

        for (index in letters.indices) {
            if (patternVowels.matcher(letters[index].toString()).matches()) {
                onlyOneVowel = true
                check = index
            }
        }
        if (onlyOneVowel) return check

        // if this has interleaved
        check = -1
        for (index in letters.indices) {
            if (patternVowelsAccent.matcher(letters[index].toString()).matches()) {
                check = index
            }
        }
        if (check != -1) return check

        check = -1
        for (index in letters.indices) {
            if (patternOpenVowels.matcher(letters[index].toString()).matches()) {
                check = index
            }
        }
        return check
    }

    /**
     * Get last syllable
     *
     * @param word
     */
    fun getLastSyllable(word: String) = getSyllables(word).last()

    /**
     * Get last vowel
     *
     * @param word
     * @return
     */
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
