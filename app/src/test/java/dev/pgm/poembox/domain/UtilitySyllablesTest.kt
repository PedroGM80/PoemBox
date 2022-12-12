package dev.pgm.poembox.domain

import org.junit.Assert.*

import org.junit.Test

class UtilitySyllablesTest {


    @Test
    fun getSyllables() {
        assertEquals(arrayListOf("ca","ca","hue","te"), UtilitySyllables().getSyllables("cacahuete"))
    }


    @Test
    fun isVowel() {
        assertEquals(true,UtilitySyllables().isVowel('a'))
        assertEquals(false,UtilitySyllables().isVowel('b'))
    }

    @Test
    fun stressed() {
        assertEquals(2,UtilitySyllables().stressed(listOf("ca","ca","hue","te")))
    }

    @Test
    fun getStressedVowelIndex() {
        assertEquals(1,UtilitySyllables().getStressedVowelIndex("bor"))
    }


}