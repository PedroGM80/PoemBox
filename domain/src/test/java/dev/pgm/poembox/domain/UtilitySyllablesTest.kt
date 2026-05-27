package dev.pgm.poembox.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UtilitySyllablesTest {

    private lateinit var sut: UtilitySyllables

    @Before
    fun setUp() {
        sut = UtilitySyllables()
    }

    // ── Casos básicos ─────────────────────────────────────────────────────────

    @Test
    fun `getSyllables cacahuete`() {
        assertEquals(arrayListOf("ca", "ca", "hue", "te"), sut.getSyllables("cacahuete"))
    }

    @Test
    fun `isVowel vowels and consonants`() {
        "aeiouáéíóú".forEach { assertTrue("'$it' debe ser vocal", sut.isVowel(it)) }
        // y se trata como vocal en el sistema (closeVowels)
        "bcdfghjklmnpqrstvwxz".forEach { assertTrue("'$it' no es vocal", !sut.isVowel(it)) }
    }

    @Test
    fun `stressed returns penultimate for plain word`() {
        // "ca-ca-hue-te" → llana → tónica en posición 2 (antepenúltima posición 2 de 0..3)
        assertEquals(2, sut.stressed(listOf("ca", "ca", "hue", "te")))
    }

    @Test
    fun `stressed returns last for acute word without accent mark`() {
        // "can-tor" → aguda sin tilde → tónica en última
        assertEquals(1, sut.stressed(listOf("can", "tor")))
    }

    @Test
    fun `stressed returns index of explicit accent`() {
        // "plás-ti-co" → tilde en primera → índice 0
        assertEquals(0, sut.stressed(listOf("plás", "ti", "co")))
    }

    @Test
    fun `getStressedVowelIndex single vowel syllable`() {
        assertEquals(1, sut.getStressedVowelIndex("bor"))
    }

    // ── Palabras con diptongo (deben ser 1 sílaba juntas) ────────────────────

    @Test
    fun `getSyllables diptongo ie - bien`() {
        // bien → 1 sílaba
        assertEquals(1, sut.getSyllables("bien").size)
    }

    @Test
    fun `getSyllables diptongo ua - agua`() {
        // a-gua → 2 sílabas
        assertEquals(arrayListOf("a", "gua"), sut.getSyllables("agua"))
    }

    @Test
    fun `getSyllables diptongo io - radio`() {
        // ra-dio → 2 sílabas
        assertEquals(2, sut.getSyllables("radio").size)
    }

    // ── Palabras con hiato (vocales que forman sílabas distintas) ────────────

    @Test
    fun `getSyllables hiato ai - pais`() {
        // pa-ís → 2 sílabas (hiato por tilde en vocal cerrada)
        assertEquals(2, sut.getSyllables("país").size)
    }

    @Test
    fun `getSyllables hiato eo - poeta`() {
        // po-e-ta → 3 sílabas (hiato entre dos vocales abiertas)
        assertEquals(3, sut.getSyllables("poeta").size)
    }

    @Test
    fun `getSyllables hiato ao - caos`() {
        // ca-os → 2 sílabas
        assertEquals(2, sut.getSyllables("caos").size)
    }

    // ── Grupos consonánticos inseparables (tr, gr, pr, br…) ──────────────────

    @Test
    fun `getSyllables grupo tr - teatro`() {
        // te-a-tro → 3 sílabas
        assertEquals(arrayListOf("te", "a", "tro"), sut.getSyllables("teatro"))
    }

    @Test
    fun `getSyllables grupo pr - premio`() {
        // pre-mio → 2 sílabas
        assertEquals(2, sut.getSyllables("premio").size)
    }

    @Test
    fun `getSyllables grupo bl - tabla`() {
        // ta-bla → 2 sílabas
        assertEquals(arrayListOf("ta", "bla"), sut.getSyllables("tabla"))
    }

    // ── Palabras con ll y ch (conversiones internas) ──────────────────────────

    @Test
    fun `getSyllables ll - llave`() {
        // lla-ve → 2 sílabas
        assertEquals(2, sut.getSyllables("llave").size)
    }

    @Test
    fun `getSyllables ch - noche`() {
        // no-che → 2 sílabas
        assertEquals(arrayListOf("no", "che"), sut.getSyllables("noche"))
    }

    // ── Palabras agudas, llanas y esdrújulas ─────────────────────────────────

    @Test
    fun `getSyllables aguda - cancion`() {
        // can-ción → 2 sílabas
        assertEquals(2, sut.getSyllables("canción").size)
    }

    @Test
    fun `getSyllables esdrujula - musica`() {
        // mú-si-ca → 3 sílabas
        assertEquals(3, sut.getSyllables("música").size)
    }

    @Test
    fun `getSyllables llana - casa`() {
        // ca-sa → 2 sílabas
        assertEquals(arrayListOf("ca", "sa"), sut.getSyllables("casa"))
    }

    // ── getLastSyllable ───────────────────────────────────────────────────────

    @Test
    fun `getLastSyllable verde`() {
        assertEquals("de", sut.getLastSyllable("verde"))
    }

    @Test
    fun `getLastSyllable amor`() {
        assertEquals("mor", sut.getLastSyllable("amor"))
    }

    // ── getLastVowel ──────────────────────────────────────────────────────────

    @Test
    fun `getLastVowel mar returns a`() {
        assertEquals("a", sut.getLastVowel("mar"))
    }

    @Test
    fun `getLastVowel verde returns e`() {
        assertEquals("e", sut.getLastVowel("verde"))
    }
}
