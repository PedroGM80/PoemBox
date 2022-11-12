package dev.pgm.poembox.businessLogic

import java.util.Locale
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
    private val openVowels = charArrayOf('a', 'á', 'e', 'é', 'o', 'ó')
    private val closeVowels = charArrayOf('i', 'u', 'ü', 'y')
    private val closeVowelsAccent = charArrayOf('í', 'ú')
    private val patternAccent: Pattern = Pattern.compile(".*([áéíóú]).*")
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
    val pattern : Pattern
        get() {
            val regex = StringBuffer(otherCase[0].code)
            for (i in 1 until otherCase.size) {
                regex.append("|").append(otherCase[i])
            }
            return Pattern.compile(regex.toString())
        }


    fun getSyllables(aWord: String?): ArrayList<String> {
        var word = aWord
        word = format(word)
        var cut: Int
        var syllable: String
        val syllables = ArrayList<String>()
        while (word!!.isNotEmpty()) {
            cut = next(word) + 1
            syllable = unformat(word.substring(0, cut))
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

            return if (isVowel(charsWord[lastLetter]) && isHiatus(charsWord[vowel], charsWord[lastLetter])) {
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
            val groupDoubleConsonant = arrayOf("tr", "gr", "pr", "br", "bl", "fr", "fl", "cl", "dr", "pl")
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

    private fun lastVowel(vocal: Int, a: CharArray): Boolean {
        for (i in vocal + 1 until a.size) {
            if (isVowel(a[i])) {
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

    private fun isVowel(l: Char): Boolean {
        for (vowel in vowels) {
            if (l.lowercaseChar() == vowel) return true
        }
        return false
    }

    private fun isConsonant(letter: Char)= !isVowel(letter)


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

    private fun unformat(aWord: String?): String {
        var word = aWord
        if (word == null) word = ""
        for (i in conversions.indices) {
            word = word!!.replace(conversions[i][1], conversions[i][0])
        }
        word = word!!.replace("¬", "h")
        return word
    }


    fun stressed(syllables: List<String?>): Int {
        if (syllables.size == 1) return 0

        for (index in syllables.indices) {
            if (patternAccent.matcher(syllables[index].toString()).matches()) return index//have accent is tonic
        }
        val last = syllables[syllables.size - 1]
        return when {
            patternVowelsCaseNCaseS.matcher(last.toString()).matches() -> syllables.size - 2 // plain
            else -> syllables.size - 1//acute
        }
    }


    fun stressedVowel(syllable: String): Int {
        val letter = syllable.lowercase(Locale.getDefault()).toCharArray()
        var ret = -1
        var onlyOneVowel = false
        val patternVowels = Pattern.compile("[aeiouáéíóú]")
        val patternVowelsAccent = Pattern.compile("[áéíóú]")
        val patternOpenVowels = Pattern.compile("[aáeéoó]")


        for (index in letter.indices) {
            val stringBuffer = StringBuffer()
            stringBuffer.append(letter[index])
            if (patternVowels.matcher(stringBuffer).matches()) {
                onlyOneVowel = true
                ret = index
            }
        }
        if (onlyOneVowel) return ret

        // if this has interleaved
        ret = -1
        for (index in letter.indices) {
            val stringBuffer = StringBuffer()
            stringBuffer.append(letter[index])
            if (patternVowelsAccent.matcher(stringBuffer).matches()) {
                ret = index
            }
        }
        if (ret != -1) return ret

        ret = -1
        for (index in letter.indices) {
            val stringBuffer = StringBuffer()
            stringBuffer.append(letter[index])
            if (patternOpenVowels.matcher(stringBuffer).matches()) {
                ret = index
            }
        }
        return ret
    }
}


fun main() {
    val testUtil = UtilitySyllables()
    val resultA = testUtil.getSyllables("cacahuate")
    val resultA2 = testUtil.getSyllables("cacahuete")
    val resultB = testUtil.getSyllables("Verónica")
    val resultC = testUtil.getSyllables("Timón")
    val resultD = testUtil.getSyllables("Cáliz")
    val resultE = testUtil.getSyllables("Cámara")

    println(resultA)
    println(resultA2)
    println(resultB)
    println(resultC)
    println(resultD)
    println(resultE)
}