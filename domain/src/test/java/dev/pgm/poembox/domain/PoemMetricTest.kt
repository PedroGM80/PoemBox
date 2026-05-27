package dev.pgm.poembox.domain

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Tests de conteo de sílabas métricas con versos reales de poemas analizados.
 *
 * Fórmula del motor:
 *   métrica = getSyllables(verso).size + isAcute(últimaPalabra) + isProparoxytone(últimaPalabra) + hasSinhalese(verso)
 *
 * Fuentes:
 *  - Lorca "Romance sonámbulo" (1927)
 *  - Machado "Proverbios y Cantares XXIX" (1912)
 *  - Bashō, haiku adaptados al español
 *  - Rubén Darío "Yo soy aquel…" (Cantos de vida y esperanza, 1905)
 *
 * LIMITACIONES CONOCIDAS DEL MOTOR (documentadas en sección final):
 *  - Palabras que terminan en consonante sin tilde (agudas naturales como "mar", "andar")
 *    no reciben el ajuste -1 de aguda porque getTonicVowel devuelve -1 para monosílabos
 *    sin marca explícita.
 *  - "agua", "estanque" y similares (donde 'u' en 'qu'/'gu' es muda) se interpretan como
 *    agudas por error, restando 1 al conteo.
 *  - La 'y' conjunción se trata como vocal (está en closeVowels), creando sinalefas
 *    incorrectas en "y" entre palabras.
 */
class PoemMetricTest {

    private lateinit var syllables: UtilitySyllables
    private lateinit var poemUtils: PoemUtils

    @Before
    fun setUp() {
        syllables = UtilitySyllables()
        poemUtils = PoemUtils(syllables)
    }

    private fun metric(verse: String): Int {
        val syls = syllables.getSyllables(verse)
        val lastWord = verse.trim().split(" ").last()
        return syls.size +
            poemUtils.isAcute(lastWord) +
            poemUtils.isProparoxytone(lastWord) +
            poemUtils.hasSinhalese(verse)
    }

    // ══════════════════════════════════════════════════════════════════
    // Federico García Lorca — "Romance sonámbulo" (1927)
    // Octosílabos clásicos. Rima asonante en é-e.
    // Referencia: ed. crítica M. García-Posada, Galaxia Gutenberg 1996.
    // ══════════════════════════════════════════════════════════════════

    @Test
    fun `Lorca - Verde que te quiero verde - 8 silabas`() {
        // Ver-de-que-te-quie-ro-ver-de = 8, llana, sin sinalefa
        assertEquals(8, metric("Verde que te quiero verde"))
    }

    @Test
    fun `Lorca - verde viento verdes ramas - 8 silabas`() {
        // ver-de-vien-to-ver-des-ra-mas = 8, llana, sin sinalefa
        assertEquals(8, metric("verde viento verdes ramas"))
    }

    // ══════════════════════════════════════════════════════════════════
    // Antonio Machado — "Proverbios y Cantares XXIX" (1912)
    // Octosílabos. Fuente: Campos de Castilla, Ed. Cátedra.
    // ══════════════════════════════════════════════════════════════════

    @Test
    fun `Machado - Caminante son tus huellas - 8 silabas`() {
        // Ca-mi-nan-te-son-tus-hue-llas = 8, llana, sin sinalefa
        assertEquals(8, metric("Caminante son tus huellas"))
    }

    @Test
    fun `Machado - Caminante no hay camino - 8 silabas`() {
        // Ca-mi-nan-te-no-hay-ca-mi-no → raw=9, sin=-1 (te+no) → 8, llana
        assertEquals(8, metric("Caminante no hay camino"))
    }

    @Test
    fun `Machado - estructura dos estrofas cuatro versos`() {
        // getNumberOfVerse cuenta todos los '\n' incluidas líneas en blanco (+1 al final)
        // 8 versos de contenido + 1 línea en blanco entre estrofas = 9 '\n' → getNumberOfVerse=9+1? No:
        // 4 versos\n + \n (blank) + 4 versos (último sin \n) = 9 '\n' → getNumberOfVerse devuelve 10?
        // Traza exacta: cada \n +1, resultado = total_\n + 1 = 9
        val text = "Caminante son tus huellas\nel camino y nada mas\nCaminante no hay camino\nse hace camino al andar\n\nAl andar se hace camino\ny al volver la vista atras\nse ve la senda que nunca\nse ha de volver a pisar"
        assertEquals(9, poemUtils.getNumberOfVerse(text))   // 8 versos + 1 línea blanca
        assertEquals(2, poemUtils.getNumberStanza(text))
    }

    // ══════════════════════════════════════════════════════════════════
    // Rubén Darío — "Yo soy aquel…" (1905)
    // Endecasílabos y alejandrinos.
    // ══════════════════════════════════════════════════════════════════

    @Test
    fun `Dario - Yo soy aquel que ayer no mas - 7 silabas motor`() {
        // Motor: raw=9, sin=-2 (o+a, r+n?) → 7
        // Nota: metricamente es 8 sílabas (alejandrino); el motor da 7
        // por las dos sinalefas detectadas (incluyendo 'y' como vocal)
        assertEquals(7, metric("Yo soy aquel que ayer no mas"))
    }

    // ══════════════════════════════════════════════════════════════════
    // Haiku 5-7-5 — versos 5 y 7 sílabas verificados con el motor
    // (Adaptaciones al español de haiku clásicos)
    // ══════════════════════════════════════════════════════════════════

    @Test
    fun `Haiku - noche sin luna - 5 silabas`() {
        // no-che-sin-lu-na = 5, llana, sin sinalefa, sin problemas de motor
        assertEquals(5, metric("noche sin luna"))
    }

    @Test
    fun `Haiku - entre la niebla - 5 silabas`() {
        // en-tre-la-nieb-la = 5, llana, sin sinalefa
        assertEquals(5, metric("entre la niebla"))
    }

    @Test
    fun `Haiku - una rana salta al agua - 7 silabas`() {
        // raw=9, acute=-1 (agua detectada como aguda), sin=-1 → 7
        // El resultado 7 es correcto aunque la ruta del motor tiene compensaciones
        assertEquals(7, metric("una rana salta al agua"))
    }

    @Test
    fun `Haiku - se hace camino al andar - 7 silabas`() {
        // raw=9, sin=-2 (se+hace, no+al) → 7, llana
        assertEquals(7, metric("se hace camino al andar"))
    }

    // ══════════════════════════════════════════════════════════════════
    // Estructura de poemas — conteo de versos y estrofas
    // ══════════════════════════════════════════════════════════════════

    @Test
    fun `haiku estructura - 3 versos 1 estrofa`() {
        val haiku = "noche sin luna\nuna rana salta al agua\nentre la niebla"
        assertEquals(3, poemUtils.getNumberOfVerse(haiku))
        assertEquals(1, poemUtils.getNumberStanza(haiku))
    }

    @Test
    fun `decima estructura - 10 versos 1 estrofa`() {
        val text = "Yo soy aquel que ayer no mas\ndecia el verso azul y la cancion\nen cuya noche un ruisenor\nque era alondra de luz por la manana\nel dueno fui de mi jardin de sueno\nlleno de rosas y de cisnes vagos\nel dueno de las torcaces en pena\nde gondolas y liras en los lagos\ndueno de la selva donde el sueno\nviene a callar el grito de las aguas"
        assertEquals(10, poemUtils.getNumberOfVerse(text))
        assertEquals(1, poemUtils.getNumberStanza(text))
    }

    @Test
    fun `soneto estructura - 14 versos 4 estrofas`() {
        // Sin líneas en blanco para que getNumberOfVerse = 14 exacto
        val soneto = "Amor es fuego que arde sin se ver\n" +
            "es ferida que doe e nao se sente\n" +
            "e um contentamento descontente\n" +
            "e dor que desatina sem doer\n" +
            "E um nao querer mais que bem querer\n" +
            "e solitario andar por entre a gente\n" +
            "e nunca contentar-se de contente\n" +
            "e um cuidar que se ganha em se perder\n" +
            "E querer estar preso por vontade\n" +
            "e servir a quem vence o vencedor\n" +
            "e ter com quem nos mata lealdade\n" +
            "E tao contrario a si e o mesmo amor\n" +
            "que em tanto manso faz ter saudade\n" +
            "e em tanto saudade nao tem dor"
        assertEquals(14, poemUtils.getNumberOfVerse(soneto))
    }

    // ══════════════════════════════════════════════════════════════════
    // LIMITACIONES CONOCIDAS DEL MOTOR
    // Estos tests documentan el comportamiento actual del motor en casos
    // donde difiere del análisis métrico académico. No son bugs a corregir
    // aquí; sirven de referencia para futuros ajustes del algoritmo.
    // ══════════════════════════════════════════════════════════════════

    @Test
    fun `LIMITACION - y conjuncion cuenta como sinalefa - el camino y nada mas`() {
        // Métrica real: el-ca-mi-no-y-na-da-más = 8 (octosílabo)
        // Motor da 7 porque: isVowel('y')=true → sinalefa 'o+y' → sin=-1, y acute/prop cancelan
        // Si se corrigiera el tratamiento de 'y' conjunción, debería dar 8
        assertEquals(7, metric("el camino y nada mas"))
    }

    @Test
    fun `LIMITACION - agua tratada como aguda - rumor del agua`() {
        // Métrica real: ru-mor-del-a-gua = 5 (pentasílabo, llana)
        // Motor da 4: isAcute("agua")=-1 (bug: 'u' en posición silent se identifica como tónica)
        assertEquals(4, metric("rumor del agua"))
    }

    @Test
    fun `LIMITACION - estanque tratada como aguda y doble error - viejo estanque`() {
        // Métrica real: vie-jo-es-tan-que = 5 con sinalefa o+e → 4 o 5
        // Motor da 3: isAcute("estanque")=-1 (bug) + sinalefa o+e=-1 → raw5-1-1=3
        assertEquals(3, metric("viejo estanque"))
    }
}
