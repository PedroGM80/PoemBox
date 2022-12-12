package dev.pgm.poembox.domain

import org.junit.Assert.assertEquals
import org.junit.Test


class PoemUtilsTest {
    @Test
    fun getNumberOfVerseIsCorrect() {
        val verses = """"un verso 
            otro verso
        """.trimMargin()
        assertEquals(2, PoemUtils().getNumberOfVerse(verses))
    }

    @Test
    fun getNumberOfStanzaIsCorrect() {
        val verses = """"un verso cualquiera
            |otro verso
            |
            |"cuarto verso
            |quinto verso
        """.trimMargin()
        assertEquals(2, PoemUtils().getNumberStanza(verses))
    }

    @Test
    fun getIsProparoxytone() {
        val word = "plástico"
        val wordB = "camión"
        assertEquals(1, PoemUtils().isProparoxytone(word))
        assertEquals(0, PoemUtils().isProparoxytone(wordB))
    }

    @Test
    fun getIsAcute() {
        val word = "plástico"
        val wordB = "camión"
        assertEquals(0, PoemUtils().isAcute(word))
        assertEquals(1, PoemUtils().isAcute(wordB))
    }

    @Test
    fun getNumberHasSinhalese() {
        val verse = "cada osa asa"
        val verseB = "cada hilo hila"
        val verseC = "cada dia es distinto"
        var verseD = "Un coche rojo"
        assertEquals(-2, PoemUtils().hasSinhalese(verse))
        assertEquals(-2, PoemUtils().hasSinhalese(verseB))
        assertEquals(-1, PoemUtils().hasSinhalese(verseC))
        assertEquals(0, PoemUtils().hasSinhalese(verseD))
    }

    @Test
    fun getTextEnjambment() {
        val verse = "sonrisas, ni las llamas"
        val verseB = "de gris… Y mi barba es blanca"

        assertEquals(
            """Enjambment is soft, light and characterized by the great fluidity
                            | it brings to the poem. It can be read practically like a prose poetic
                            |  text with all its exceptions, of course. It brings dynamism to 
                            |  the poem and very fast musical movements.""",
            PoemUtils().getEnjambment(verse)
        )
        assertEquals(
            "Enjambment is abrupt and gives rise to a syncopated, fast, violent rhythm.",
            PoemUtils().getEnjambment(verseB)
        )

    }
}