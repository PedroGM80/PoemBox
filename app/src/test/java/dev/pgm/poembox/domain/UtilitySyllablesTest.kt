package dev.pgm.poembox.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class UtilitySyllablesTest {


    @Test
    fun getSyllables() {
        assertEquals(
            arrayListOf("ca", "ca", "hue", "te"),
            UtilitySyllables().getSyllables("cacahuete")
        )
    }


    @Test
    fun isVowel() {
        assertEquals(true, UtilitySyllables().isVowel('a'))
        assertEquals(false, UtilitySyllables().isVowel('b'))
    }

    @Test
    fun stressed() {
        assertEquals(2, UtilitySyllables().stressed(listOf("ca", "ca", "hue", "te")))
    }

    @Test
    fun `stressed para monosílabo agudo devuelve índice 0`() {
        // "bor" = 1 sílaba → stressed devuelve 0 (única sílaba)
        assertEquals(0, UtilitySyllables().stressed(listOf("bor")))
    }


}