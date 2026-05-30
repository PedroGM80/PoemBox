package dev.pgm.poembox.domain

import java.util.*
import javax.inject.Inject

/**
 * Motor de sílabas para Inglés.
 * A diferencia del español, el inglés no es puramente silábico y depende de la sonoridad.
 * Esta implementación usa el algoritmo de "vowel-consonant" con reglas de excepciones
 * para aproximar el conteo de sílabas sin requerir un diccionario masivo.
 */
class EnglishSyllableEngine @Inject constructor() : SyllableEngine {

    override fun getSyllables(word: String): List<String> {
        val cleanWord = word.lowercase(Locale.ENGLISH).replace(Regex("[^a-z]"), "")
        if (cleanWord.isEmpty()) return emptyList()
        
        // El inglés es muy difícil de separar físicamente por sílabas sin diccionario.
        // Aquí usamos una aproximación fonética para el conteo.
        val count = countSyllables(cleanWord)
        
        // Para la UI, devolvemos la palabra si no podemos separarla con precisión,
        // o una aproximación.
        return if (count <= 1) listOf(cleanWord) else splitApproximate(cleanWord, count)
    }

    override fun getStressedSyllableIndex(syllables: List<String>): Int {
        // En inglés el acento es muy variable. 
        // Regla general: En palabras de 2 sílabas, 80% recae en la primera (sustantivos).
        if (syllables.size <= 1) return 0
        return 0 
    }

    override fun isVowel(letter: Char): Boolean {
        return letter in "aeiouy"
    }

    private fun countSyllables(word: String): Int {
        var count = 0
        var isLastVowel = false
        val vowels = "aeiouy"
        
        for (i in word.indices) {
            val isVowel = vowels.contains(word[i])
            if (isVowel && !isLastVowel) {
                count++
            }
            isLastVowel = isVowel
        }
        
        // Excepciones comunes:
        // 1. 'e' muda al final
        if (word.endsWith("e") && count > 1) {
            // Pero no si termina en 'le' (ej: apple)
            if (!word.endsWith("le")) {
                count--
            }
        }
        
        return if (count == 0) 1 else count
    }

    private fun splitApproximate(word: String, count: Int): List<String> {
        // Separación heurística simple para visualización
        // En una app profesional pro, aquí usaríamos un diccionario o 
        // un modelo de ML ligero.
        return listOf(word) // Por ahora devolvemos la palabra completa
    }
}
