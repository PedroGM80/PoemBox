package dev.pgm.poembox.ai

import dev.pgm.poembox.domain.UtilitySyllables
import dev.pgm.poembox.presentation.ai.RhymeSuggester
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for RhymeSuggester.analyze().
 * All tests are deterministic and run on the JVM without Android context.
 */
class RhymeSuggesterTest {

    private lateinit var sut: RhymeSuggester

    @Before
    fun setUp() {
        sut = RhymeSuggester(UtilitySyllables())
    }

    // ── Null / blank verse inputs ──────────────────────────────────────────────

    @Test
    fun `analyze empty string returns null`() {
        assertNull(sut.analyze(""))
    }

    @Test
    fun `analyze blank verse with spaces returns null`() {
        assertNull(sut.analyze("    "))
    }

    @Test
    fun `analyze whitespace-only verse returns null`() {
        assertNull(sut.analyze("\t\n  "))
    }

    // ── Single word verses ────────────────────────────────────────────────────

    @Test
    fun `analyze single word verde returns non-null result`() {
        val result = sut.analyze("verde")
        assertNotNull(result)
    }

    @Test
    fun `analyze single word verde extracts verde as lastWord`() {
        val result = sut.analyze("verde")!!
        assertEquals("verde", result.lastWord)
    }

    @Test
    fun `analyze verde asonantPattern contains only plain vowels no accent marks`() {
        val result = sut.analyze("verde")!!
        // asonantPattern must not contain á é í ó ú ü
        val accented = "áéíóúü"
        val hasAccent = result.asonantPattern.any { it in accented }
        assertTrue("asonantPattern must not contain accent marks but was: ${result.asonantPattern}", !hasAccent)
    }

    // ── Multi-word verse: last word extraction ────────────────────────────────

    @Test
    fun `analyze Verde que te quiero verde extracts verde as lastWord`() {
        val result = sut.analyze("Verde que te quiero verde")!!
        assertEquals("verde", result.lastWord)
    }

    @Test
    fun `analyze Verde que te quiero verde asonantPattern is e-e`() {
        // "verde": tonic index = penultimate (vér-de) → from "verde" → vowels e,e → "e-e"
        val result = sut.analyze("Verde que te quiero verde")!!
        assertEquals("e-e", result.asonantPattern)
    }

    // ── Llana word ending -ante ───────────────────────────────────────────────

    @Test
    fun `analyze caminante extracts caminante as lastWord`() {
        val result = sut.analyze("caminante")!!
        assertEquals("caminante", result.lastWord)
    }

    @Test
    fun `analyze caminante asonantPattern contains a-e`() {
        // ca-mi-nan-te: tonic is penultimate "nan" → from "nante" → vowels a,e → "a-e"
        val result = sut.analyze("caminante")!!
        assertEquals("a-e", result.asonantPattern)
    }

    // ── asonantPattern normalization (no accent marks) ────────────────────────

    @Test
    fun `analyze corazon asonantPattern has no accent marks`() {
        val result = sut.analyze("corazón")!!
        val accented = "áéíóúü"
        val hasAccent = result.asonantPattern.any { it in accented }
        assertTrue("asonantPattern must not contain accent marks", !hasAccent)
    }

    @Test
    fun `analyze corazon lastWord is corazon without tilde`() {
        // The regex [^a-záéíóúüñ] strips punctuation but preserves accented letters
        // then lowercase is applied → "corazón" stays "corazón"
        val result = sut.analyze("corazón")!!
        assertEquals("corazón", result.lastWord)
    }

    // ── Punctuation stripping ─────────────────────────────────────────────────

    @Test
    fun `analyze amor with trailing comma extracts amor as lastWord`() {
        val result = sut.analyze("amor,")!!
        assertEquals("amor", result.lastWord)
    }

    @Test
    fun `analyze verse with exclamation extracts clean lastWord`() {
        val result = sut.analyze("dame luz!")!!
        assertEquals("luz", result.lastWord)
    }

    @Test
    fun `analyze verse ending in period strips period from lastWord`() {
        val result = sut.analyze("verde.")!!
        assertEquals("verde", result.lastWord)
    }

    // ── lastWord is always lowercase ──────────────────────────────────────────

    @Test
    fun `analyze uppercase verse produces lowercase lastWord`() {
        val result = sut.analyze("AMOR")!!
        assertEquals("amor", result.lastWord)
    }

    @Test
    fun `analyze mixed case produces lowercase lastWord`() {
        val result = sut.analyze("La Verde Vida")!!
        assertEquals("vida", result.lastWord)
    }

    // ── suggestions list constraints ──────────────────────────────────────────

    @Test
    fun `analyze verde suggestions list is not empty`() {
        val result = sut.analyze("verde")!!
        assertTrue("suggestions must not be empty for common word", result.suggestions.isNotEmpty())
    }

    @Test
    fun `analyze amor suggestions list size is at most 8`() {
        val result = sut.analyze("amor")!!
        assertTrue("suggestions must have at most 8 entries", result.suggestions.size <= 8)
    }

    @Test
    fun `analyze suggestions size never exceeds 8 for any common word`() {
        val verses = listOf("luna", "vida", "sol", "viento", "cielo", "noche", "tierra")
        for (verse in verses) {
            val result = sut.analyze(verse)!!
            assertTrue(
                "suggestions for '$verse' exceeded 8: ${result.suggestions.size}",
                result.suggestions.size <= 8
            )
        }
    }

    // ── Excluded word does not appear in its own suggestions ─────────────────

    @Test
    fun `analyze amor does not include amor in suggestions`() {
        val result = sut.analyze("amor")!!
        assertTrue(
            "excluded word 'amor' must not appear in suggestions",
            "amor" !in result.suggestions
        )
    }

    @Test
    fun `analyze verde does not include verde in suggestions`() {
        val result = sut.analyze("verde")!!
        assertTrue(
            "excluded word 'verde' must not appear in suggestions",
            "verde" !in result.suggestions
        )
    }

    // ── consonantPattern format ───────────────────────────────────────────────

    @Test
    fun `analyze verde consonantPattern starts with dash`() {
        val result = sut.analyze("verde")!!
        assertTrue(
            "consonantPattern must start with '-' but was: ${result.consonantPattern}",
            result.consonantPattern.startsWith("-")
        )
    }

    @Test
    fun `analyze amor consonantPattern starts with dash`() {
        val result = sut.analyze("amor")!!
        assertTrue(result.consonantPattern.startsWith("-"))
    }
}
