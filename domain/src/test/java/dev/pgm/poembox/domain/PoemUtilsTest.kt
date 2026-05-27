package dev.pgm.poembox.domain

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PoemUtilsTest {

    private lateinit var sut: PoemUtils

    @Before
    fun setUp() {
        sut = PoemUtils(UtilitySyllables())
    }

    // ── getNumberOfVerse ──────────────────────────────────────────────────────

    @Test
    fun `getNumberOfVerse two lines`() {
        val text = "un verso\notro verso"
        assertEquals(2, sut.getNumberOfVerse(text))
    }

    @Test
    fun `getNumberOfVerse single line`() {
        assertEquals(1, sut.getNumberOfVerse("un solo verso"))
    }

    // ── getNumberStanza ───────────────────────────────────────────────────────

    @Test
    fun `getNumberStanza two stanzas separated by blank line`() {
        val text = "verso uno\nverso dos\n\nverso tres\nverso cuatro"
        assertEquals(2, sut.getNumberStanza(text))
    }

    @Test
    fun `getNumberStanza blank text returns zero`() {
        assertEquals(0, sut.getNumberStanza("   "))
    }

    // ── isProparoxytone ───────────────────────────────────────────────────────

    @Test
    fun `isProparoxytone esdrujula returns 1`() {
        // "plástico" → tilde en antepenúltima → esdrújula → +1
        assertEquals(1, sut.isProparoxytone("plástico"))
    }

    @Test
    fun `isProparoxytone palabra con tilde returns 1`() {
        // La función devuelve 1 para cualquier palabra con tilde explícita en la sílaba tónica
        // (incluye agudas como "camión"), no solo esdrújulas
        assertEquals(1, sut.isProparoxytone("camión"))
        assertEquals(1, sut.isProparoxytone("canción"))
    }

    @Test
    fun `isProparoxytone llana sin tilde returns 0`() {
        assertEquals(0, sut.isProparoxytone("canto"))
        assertEquals(0, sut.isProparoxytone("verde"))
    }

    // ── isAcute ───────────────────────────────────────────────────────────────

    @Test
    fun `isAcute aguda returns -1`() {
        // Palabra aguda → tónica en última sílaba → devuelve -1 (suma al conteo)
        assertEquals(-1, sut.isAcute("camión"))
    }

    @Test
    fun `isAcute llana returns 0`() {
        assertEquals(0, sut.isAcute("canto"))
    }

    @Test
    fun `isAcute esdrujula returns 0`() {
        // Esdrújula → tónica no es la última → 0
        assertEquals(0, sut.isAcute("plástico"))
    }

    // ── hasSinhalese (sinalefa) ───────────────────────────────────────────────

    @Test
    fun `hasSinhalese two sinalefas`() {
        // "cada osa asa": a+o, a+a → -2
        assertEquals(-2, sut.hasSinhalese("cada osa asa"))
    }

    @Test
    fun `hasSinhalese h counts as vowel start`() {
        // "cada hilo hila": a+h(i), o+h(i) → -2
        assertEquals(-2, sut.hasSinhalese("cada hilo hila"))
    }

    @Test
    fun `hasSinhalese one sinalefa`() {
        // "cada dia es distinto": a+e → -1
        assertEquals(-1, sut.hasSinhalese("cada dia es distinto"))
    }

    @Test
    fun `hasSinhalese none`() {
        assertEquals(0, sut.hasSinhalese("un coche rojo"))
    }

    // ── getEnjambment ─────────────────────────────────────────────────────────

    @Test
    fun `getEnjambment suave coma despues de silaba 5`() {
        // "verde que te quiero, verde": verde(2)+que(1)+te(1)+quiero,(2)=6 >= 5 → suave
        val result = sut.getEnjambment("verde que te quiero, verde")
        assertEquals(
            "Encabalgamiento suave: aporta gran fluidez al poema, que puede leerse " +
            "casi como prosa poética. Otorga dinamismo y movimientos musicales ágiles.",
            result
        )
    }

    @Test
    fun `getEnjambment abrupto coma antes de silaba 5`() {
        // "de gris, y mi barba": de(1)+gris,(1)=2 < 5 → abrupto
        val result = sut.getEnjambment("de gris, y mi barba es blanca")
        assertEquals(
            "Encabalgamiento abrupto: crea un ritmo sincopado, rápido e intenso.",
            result
        )
    }

    @Test
    fun `getEnjambment sin encabalgamiento`() {
        val result = sut.getEnjambment("verde que te quiero verde")
        assertEquals("Sin encabalgamiento.", result)
    }
}
