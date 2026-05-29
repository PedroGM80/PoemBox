package dev.pgm.poembox.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Edge-case tests for PoemUtils that complement the existing PoemUtilsTest.
 * Coverage: empty/single-element inputs, multi-stanza counting, accent handling on
 * empty words, sinalefa boundary conditions, and all three enjambment branches.
 */
class PoemUtilsEdgeCasesTest {

    private lateinit var sut: PoemUtils

    @Before
    fun setUp() {
        sut = PoemUtils(UtilitySyllables())
    }

    // ── getNumberOfVerse ──────────────────────────────────────────────────────

    @Test
    fun `getNumberOfVerse empty string returns 1`() {
        // Even an empty string represents a single (empty) verse line
        assertEquals(1, sut.getNumberOfVerse(""))
    }

    @Test
    fun `getNumberOfVerse three lines returns 3`() {
        assertEquals(3, sut.getNumberOfVerse("line1\nline2\nline3"))
    }

    @Test
    fun `getNumberOfVerse text with trailing newline counts the blank as a line`() {
        // "a\nb\n" has 2 newline chars → returns 3
        assertEquals(3, sut.getNumberOfVerse("a\nb\n"))
    }

    // ── getNumberStanza ───────────────────────────────────────────────────────

    @Test
    fun `getNumberStanza empty string returns 0`() {
        assertEquals(0, sut.getNumberStanza(""))
    }

    @Test
    fun `getNumberStanza single word returns 1`() {
        assertEquals(1, sut.getNumberStanza("solo"))
    }

    @Test
    fun `getNumberStanza two stanzas a-b blank c-d`() {
        val text = "a\nb\n\nc\nd"
        assertEquals(2, sut.getNumberStanza(text))
    }

    @Test
    fun `getNumberStanza three stanzas each separated by blank line`() {
        val text = "a\n\nb\n\nc"
        assertEquals(3, sut.getNumberStanza(text))
    }

    @Test
    fun `getNumberStanza leading blank lines do not add stanza`() {
        val text = "\n\na\nb"
        assertEquals(1, sut.getNumberStanza(text))
    }

    @Test
    fun `getNumberStanza trailing blank lines do not add stanza`() {
        val text = "a\nb\n\n"
        assertEquals(1, sut.getNumberStanza(text))
    }

    // ── isAcute ───────────────────────────────────────────────────────────────

    @Test
    fun `isAcute empty string returns 0 without crash`() {
        // getTonicSyllable returns null for empty → isAcute returns 0
        assertEquals(0, sut.isAcute(""))
    }

    @Test
    fun `isAcute amor without tilde ends in consonant r returns 0`() {
        // "amor": no tilde, last letter is 'r' which is NOT in [aeiouns], so
        // getTonicVowel returns -1 (the pattern only detects plain vowels/n/s endings
        // for llana default — without tilde and ending in 'r' there is no tonicVowel
        // resolved), hence isAcute returns 0 (null tonicSyllable path).
        assertEquals(0, sut.isAcute("amor"))
    }

    @Test
    fun `isAcute verde is llana returns 0`() {
        assertEquals(0, sut.isAcute("verde"))
    }

    @Test
    fun `isAcute cancion with tilde is acute returns -1`() {
        assertEquals(-1, sut.isAcute("canción"))
    }

    // ── hasSinhalese ──────────────────────────────────────────────────────────

    @Test
    fun `hasSinhalese empty string returns 0`() {
        assertEquals(0, sut.hasSinhalese(""))
    }

    @Test
    fun `hasSinhalese single word returns 0`() {
        // Need at least 2 words for any sinalefa
        assertEquals(0, sut.hasSinhalese("amor"))
    }

    @Test
    fun `hasSinhalese two words first ends consonant no sinalefa`() {
        // "un barco": n → consonant, b → consonant → no sinalefa
        assertEquals(0, sut.hasSinhalese("un barco"))
    }

    @Test
    fun `hasSinhalese only two words first has no preceding word`() {
        // "una palabra": only 2 words, can form at most 1 sinalefa at boundary
        // 'a' (last of "una") + 'p' (first of "palabra") → 'p' is consonant → no sinalefa
        assertEquals(0, sut.hasSinhalese("una palabra"))
    }

    @Test
    fun `hasSinhalese vowel to vowel across words produces negative count`() {
        // "la era": 'a' + 'e' → vowel+vowel → -1
        assertEquals(-1, sut.hasSinhalese("la era"))
    }

    // ── getEnjambment ─────────────────────────────────────────────────────────

    @Test
    fun `getEnjambment no punctuation in verse returns sin encabalgamiento`() {
        val result = sut.getEnjambment("vuela el pájaro en el cielo")
        assertEquals("Sin encabalgamiento.", result)
    }

    @Test
    fun `getEnjambment comma after first word is abrupto`() {
        // "sí, todo lo demás": "sí" = 1 syllable < 5 → abrupto
        val result = sut.getEnjambment("sí, todo lo demás")
        assertEquals(
            "Encabalgamiento abrupto: crea un ritmo sincopado, rápido e intenso.",
            result
        )
    }

    @Test
    fun `getEnjambment comma after 5 or more syllables is suave`() {
        // "verde que te quiero, verde": verde(2)+que(1)+te(1)+quiero(2)=6 ≥ 5 → suave
        val result = sut.getEnjambment("verde que te quiero, verde")
        assertTrue(result.startsWith("Encabalgamiento suave"))
    }

    @Test
    fun `getEnjambment period triggers same logic as comma`() {
        // "sí. todo": 1 syllable < 5 → abrupto
        val result = sut.getEnjambment("sí. todo")
        assertEquals(
            "Encabalgamiento abrupto: crea un ritmo sincopado, rápido e intenso.",
            result
        )
    }
}
