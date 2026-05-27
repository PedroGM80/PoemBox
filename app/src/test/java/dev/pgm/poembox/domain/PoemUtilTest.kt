package dev.pgm.poembox.domain

import org.junit.Assert.assertEquals
import org.junit.Test


class PoemUtilsTest {
    @Test
    fun getNumberOfVerseIsCorrect() {
        val verses = """"un verso 
            otro verso
        """.trimMargin()
        assertEquals(2, PoemUtils(UtilitySyllables()).getNumberOfVerse(verses))
    }

    @Test
    fun getNumberOfStanzaIsCorrect() {
        val verses = """"un verso cualquiera
            |otro verso
            |
            |"cuarto verso
            |quinto verso
        """.trimMargin()
        assertEquals(2, PoemUtils(UtilitySyllables()).getNumberStanza(verses))
    }

    @Test
    fun getIsProparoxytone() {
        val word = "plástico"   // tiene tilde → devuelve 1
        val wordB = "camión"    // tiene tilde → devuelve 1 (la función devuelve 1 para cualquier palabra con tilde, no solo esdrújulas)
        val wordC = "canto"     // sin tilde → devuelve 0
        assertEquals(1, PoemUtils(UtilitySyllables()).isProparoxytone(word))
        assertEquals(1, PoemUtils(UtilitySyllables()).isProparoxytone(wordB))
        assertEquals(0, PoemUtils(UtilitySyllables()).isProparoxytone(wordC))
    }

    @Test
    fun getIsAcute() {
        val word = "plástico"   // esdrújula → tónica no es la última
        val wordB = "camión"    // aguda → tónica es la última → devuelve -1
        val wordC = "canto"     // llana → tónica no es la última → devuelve 0
        assertEquals(0, PoemUtils(UtilitySyllables()).isAcute(word))
        assertEquals(-1, PoemUtils(UtilitySyllables()).isAcute(wordB))
        assertEquals(0, PoemUtils(UtilitySyllables()).isAcute(wordC))
    }

    @Test
    fun getNumberHasSinhalese() {
        val verse = "cada osa asa"
        val verseB = "cada hilo hila"
        val verseC = "cada dia es distinto"
        var verseD = "Un coche rojo"
        assertEquals(-2, PoemUtils(UtilitySyllables()).hasSinhalese(verse))
        assertEquals(-2, PoemUtils(UtilitySyllables()).hasSinhalese(verseB))
        assertEquals(-1, PoemUtils(UtilitySyllables()).hasSinhalese(verseC))
        assertEquals(0, PoemUtils(UtilitySyllables()).hasSinhalese(verseD))
    }

    @Test
    fun getTextEnjambment() {
        // verde(2)+que(1)+te(1)+quiero,(2)=6 ≥ 5 → encabalgamiento suave
        val verse = "verde que te quiero, verde"
        // de(1)+gris,(1)=2 < 5 → encabalgamiento abrupto
        val verseB = "de gris, y mi barba es blanca"
        // Sin puntuación → sin encabalgamiento
        val verseC = "verde que te quiero verde"

        assertEquals(
            "Encabalgamiento suave: aporta gran fluidez al poema, que puede leerse " +
            "casi como prosa poética. Otorga dinamismo y movimientos musicales ágiles.",
            PoemUtils(UtilitySyllables()).getEnjambment(verse)
        )
        assertEquals(
            "Encabalgamiento abrupto: crea un ritmo sincopado, rápido e intenso.",
            PoemUtils(UtilitySyllables()).getEnjambment(verseB)
        )
        assertEquals(
            "Sin encabalgamiento.",
            PoemUtils(UtilitySyllables()).getEnjambment(verseC)
        )
    }
}