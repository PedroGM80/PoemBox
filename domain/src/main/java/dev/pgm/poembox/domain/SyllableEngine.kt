package dev.pgm.poembox.domain

interface SyllableEngine {
    fun getSyllables(word: String): List<String>
    fun getStressedSyllableIndex(syllables: List<String>): Int
    fun isVowel(letter: Char): Boolean
}
