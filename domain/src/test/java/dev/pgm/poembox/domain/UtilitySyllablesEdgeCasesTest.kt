package dev.pgm.poembox.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Edge-case tests for UtilitySyllables that complement the existing UtilitySyllablesTest.
 * Coverage: empty/single-char input, silent-h words, rr/x words, compound words,
 * punctuation stripping, proper nouns, long words, and the subr* guard fix.
 */
class UtilitySyllablesEdgeCasesTest {

    private lateinit var sut: UtilitySyllables

    @Before
    fun setUp() {
        sut = UtilitySyllables()
    }

    // ── Empty / null input ──────────────────────────────────────────────────────

    @Test
    fun `getSyllables empty string returns empty list`() {
        val result = sut.getSyllables("")
        assertTrue("empty string should yield empty syllable list", result.isEmpty())
    }

    @Test
    fun `getSyllables null returns empty list`() {
        val result = sut.getSyllables(null)
        assertTrue("null should yield empty syllable list", result.isEmpty())
    }

    // ── Single vowel ────────────────────────────────────────────────────────────

    @Test
    fun `getSyllables single vowel a yields one syllable`() {
        val result = sut.getSyllables("a")
        assertEquals(1, result.size)
        assertEquals("a", result[0])
    }

    @Test
    fun `getSyllables single vowel o yields one syllable`() {
        assertEquals(1, sut.getSyllables("o").size)
    }

    // ── Silent h words ──────────────────────────────────────────────────────────

    @Test
    fun `getSyllables ahora has 3 syllables a-ho-ra`() {
        // a-ho-ra: 'h' is silent, 'a' and 'o' are open vowels separated by h → hiato
        val result = sut.getSyllables("ahora")
        assertEquals("ahora should have 3 syllables", 3, result.size)
    }

    @Test
    fun `getSyllables hotel has 2 syllables ho-tel`() {
        val result = sut.getSyllables("hotel")
        assertEquals("hotel should have 2 syllables", 2, result.size)
    }

    @Test
    fun `getSyllables hielo has 2 syllables hie-lo`() {
        // hie-lo: diphthong ie, 'h' silent
        val result = sut.getSyllables("hielo")
        assertEquals("hielo should have 2 syllables", 2, result.size)
    }

    // ── Words with rr (treated as single consonant via conversion) ─────────────

    @Test
    fun `getSyllables perro has 2 syllables pe-rro`() {
        val result = sut.getSyllables("perro")
        assertEquals("perro should have 2 syllables", 2, result.size)
    }

    @Test
    fun `getSyllables tierra has 2 syllables tie-rra`() {
        // tie-rra: diphthong 'ie', rr as single consonant
        val result = sut.getSyllables("tierra")
        assertEquals("tierra should have 2 syllables", 2, result.size)
    }

    @Test
    fun `getSyllables correo has 3 syllables co-rre-o`() {
        // co-rre-o: rr single consonant, 'e' and 'o' open vowel hiatus
        val result = sut.getSyllables("correo")
        assertEquals("correo should have 3 syllables", 3, result.size)
    }

    // ── Words with x ────────────────────────────────────────────────────────────

    @Test
    fun `getSyllables examen has 3 syllables ex-a-men`() {
        val result = sut.getSyllables("examen")
        assertEquals("examen should have 3 syllables", 3, result.size)
    }

    @Test
    fun `getSyllables exito has 3 syllables ex-i-to`() {
        val result = sut.getSyllables("éxito")
        assertEquals("exito should have 3 syllables", 3, result.size)
    }

    // ── Compound / long words ───────────────────────────────────────────────────

    @Test
    fun `getSyllables contratiempo has 5 syllables con-tra-tiem-po`() {
        // con-tra-tiem-po: 'tiem' is a diphthong (ie)
        val result = sut.getSyllables("contratiempo")
        assertEquals("contratiempo should have 4 syllables", 4, result.size)
    }

    @Test
    fun `getSyllables paraguas has 3 syllables pa-ra-guas`() {
        // pa-ra-guas: 'guas' treated as 1 syllable (gü diphthong)
        val result = sut.getSyllables("paraguas")
        assertEquals("paraguas should have 3 syllables", 3, result.size)
    }

    @Test
    fun `getSyllables extraordinariamente has 8 syllables`() {
        // ex-tra-or-di-na-ria-men-te
        val result = sut.getSyllables("extraordinariamente")
        assertEquals("extraordinariamente should have 8 syllables", 8, result.size)
    }

    // ── Punctuation stripping ───────────────────────────────────────────────────

    @Test
    fun `getSyllables amor with trailing comma same count as amor`() {
        val withComma = sut.getSyllables("amor,")
        val plain = sut.getSyllables("amor")
        assertEquals("trailing comma should not change syllable count", plain.size, withComma.size)
    }

    @Test
    fun `getSyllables word with trailing period same count as without`() {
        val withDot = sut.getSyllables("verde.")
        val plain = sut.getSyllables("verde")
        assertEquals("trailing period should not change syllable count", plain.size, withDot.size)
    }

    // ── Proper nouns ────────────────────────────────────────────────────────────

    @Test
    fun `getSyllables espana has 3 syllables Es-pa-na`() {
        val result = sut.getSyllables("España")
        assertEquals("España should have 3 syllables", 3, result.size)
    }

    @Test
    fun `getSyllables garcia has 3 syllables Gar-ci-a`() {
        // Gar-cí-a: hiatus because 'í' is a stressed close vowel
        val result = sut.getSyllables("García")
        assertEquals("García should have 3 syllables", 3, result.size)
    }

    // ── subr* guard — verifies the charsWord.size >= 4 fix ─────────────────────

    @Test
    fun `getSyllables subrayar has 3 syllables sub-ra-yar`() {
        // The engine has a special guard: if word starts with 'subr', cut after index 2
        // so "sub" is the first syllable regardless of what follows.
        val result = sut.getSyllables("subrayar")
        assertEquals("subrayar should have 3 syllables (sub-ra-yar)", 3, result.size)
        assertEquals("first syllable of subrayar should be sub", "sub", result[0])
    }

    // ── getLastVowel edge cases ─────────────────────────────────────────────────

    @Test
    fun `getLastVowel on word ending in consonant returns last vowel before it`() {
        assertEquals("o", sut.getLastVowel("amor"))
    }

    @Test
    fun `getLastVowel on word ending in vowel returns that vowel`() {
        assertEquals("a", sut.getLastVowel("casa"))
    }

    @Test
    fun `getLastVowel on word with accent mark preserves the accented character`() {
        // getLastVowel does NOT normalize — it returns the raw character from the word
        assertEquals("ó", sut.getLastVowel("corazón"))
    }
}
