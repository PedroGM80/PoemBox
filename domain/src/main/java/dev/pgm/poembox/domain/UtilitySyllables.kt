package dev.pgm.poembox.domain

import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject

class UtilitySyllables @Inject constructor(
    private val factory: SyllableEngineFactory
) {
    /** Constructor sin argumentos para tests JVM (no usa Hilt). */
    constructor() : this(SyllableEngineFactory(SpanishSyllableEngine(), EnglishSyllableEngine()))

    private var currentEngine: SyllableEngine = factory.getEngine("es")
    private var currentLang: String = "es"

    fun setLanguage(lang: String) {
        if (currentLang != lang) {
            currentEngine = factory.getEngine(lang)
            currentLang = lang
        }
    }

    /**
     * Detecta el idioma del texto de forma automática y configura el motor.
     */
    fun detectAndSetLanguage(text: String) {
        val lang = detectLanguage(text)
        setLanguage(lang)
    }

    private fun detectLanguage(text: String): String {
        if (text.isBlank()) return "es"
        
        // Palabras extremadamente comunes (stop words) para detección rápida
        val englishBoosters = listOf("the", "and", "with", "from", "that", "this", "shall", "thou", "thee")
        val spanishBoosters = listOf("el", "la", "los", "las", "con", "para", "que", "del", "una")
        
        val words = text.lowercase().split(Regex("\\s+"))
        var enCount = 0
        var esCount = 0
        
        for (word in words) {
            if (word in englishBoosters) enCount++
            if (word in spanishBoosters) esCount++
        }
        
        // También buscamos caracteres exclusivos del español (ñ, tildes)
        val hasSpanishChars = text.contains(Regex("[ñáéíóúü]", RegexOption.IGNORE_CASE))
        
        return when {
            hasSpanishChars -> "es"
            enCount > esCount -> "en"
            esCount > enCount -> "es"
            else -> "es" // Default
        }
    }

    /**
     * Get syllables
     *
     * @param aWord
     * @return syllables list
     */
    fun getSyllables(aWord: String?): ArrayList<String> {
        if (aWord == null) return arrayListOf()
        return ArrayList(currentEngine.getSyllables(aWord))
    }

    /**
     * Stressed
     *
     * @param syllables
     * @return index stressed syllable
     */
    fun stressed(syllables: List<String?>): Int {
        val nonNullSyllables = syllables.filterNotNull().map { it.toString() }
        return currentEngine.getStressedSyllableIndex(nonNullSyllables)
    }

    fun isVowel(letter: Char): Boolean = currentEngine.isVowel(letter)

    /**
     * Get last syllable
     *
     * @param word
     */
    fun getLastSyllable(word: String) = getSyllables(word).lastOrNull() ?: ""

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
