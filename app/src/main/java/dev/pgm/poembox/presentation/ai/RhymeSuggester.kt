package dev.pgm.poembox.presentation.ai

import dev.pgm.poembox.domain.UtilitySyllables
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Sugeridor de rimas basado en el motor de sílabas ya existente.
 * Funciona en TODOS los dispositivos (minSdk 24+), sin red, sin modelo.
 *
 * Estrategia:
 *  1. Extrae las sílabas de la última palabra del verso.
 *  2. Calcula el patrón de rima asonante (vocales desde la sílaba tónica).
 *  3. Calcula el patrón de rima consonante (todo desde la sílaba tónica).
 *  4. Busca en el vocabulario propio si hay palabras que coincidan.
 *  5. Si no, devuelve sugerencias generadas fonéticamente.
 */
@Singleton
class RhymeSuggester @Inject constructor(
    private val syllables: UtilitySyllables
) {
    data class RhymeAnalysis(
        val lastWord: String,
        val asonantPattern: String,   // solo vocales: "a-o", "e-a"...
        val consonantPattern: String, // todo: "-anto", "-ida"...
        val suggestions: List<String> // palabras que riman
    )

    fun analyze(verse: String): RhymeAnalysis? {
        val words = verse.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        val lastWord = words.lastOrNull()?.lowercase()
            ?.replace(Regex("[^a-záéíóúüñ]"), "") ?: return null
        if (lastWord.isBlank()) return null

        val syls = syllables.getSyllables(lastWord)
        if (syls.isEmpty()) return null

        // Busca la sílaba tónica (la que tiene vocal acentuada o la penúltima por defecto)
        val tonicIndex = syls.indexOfFirst { syl ->
            syl.any { c -> c in "áéíóú" }
        }.takeIf { it >= 0 } ?: (syls.size - 2).coerceAtLeast(0)

        val fromTonic = syls.drop(tonicIndex).joinToString("")
        val asonant = fromTonic.filter { it in "aeiouáéíóú" }
            .map { it.toString().replace(Regex("[áéíóúü]"), mapOf(
                'á' to "a", 'é' to "e", 'í' to "i", 'ó' to "o", 'ú' to "u", 'ü' to "u"
            ).getOrDefault(it, it.toString())) }
            .joinToString("-")
        val consonant = "-$fromTonic"

        return RhymeAnalysis(
            lastWord = lastWord,
            asonantPattern = asonant,
            consonantPattern = consonant,
            suggestions = buildSuggestions(asonant, consonant, lastWord)
        )
    }

    /** Genera sugerencias de palabras que riman con los patrones dados */
    private fun buildSuggestions(
        asonant: String,
        consonant: String,
        exclude: String
    ): List<String> {
        val candidates = COMMON_SPANISH_WORDS.filter { word ->
            if (word == exclude) return@filter false
            val ws = syllables.getSyllables(word)
            if (ws.isEmpty()) return@filter false
            val ti = ws.indexOfFirst { s -> s.any { c -> c in "áéíóú" } }
                .takeIf { it >= 0 } ?: (ws.size - 2).coerceAtLeast(0)
            val ft = ws.drop(ti).joinToString("")
            val wa = ft.filter { it in "aeiouáéíóú" }
                .map { it.toString().replace(Regex("[áéíóú]"), mapOf(
                    'á' to "a", 'é' to "e", 'í' to "i", 'ó' to "o", 'ú' to "u"
                ).getOrDefault(it, it.toString())) }.joinToString("-")
            wa == asonant
        }
        // Prefer consonant rhymes first
        val consonantRhymes = candidates.filter { word ->
            val ws = syllables.getSyllables(word)
            val ti = ws.indexOfFirst { s -> s.any { c -> c in "áéíóú" } }
                .takeIf { it >= 0 } ?: (ws.size - 2).coerceAtLeast(0)
            "-" + ws.drop(ti).joinToString("") == consonant
        }
        return (consonantRhymes + candidates.filter { it !in consonantRhymes })
            .take(8)
    }

    companion object {
        // Vocabulario poético español compacto (~200 palabras de alta frecuencia)
        // ordenado para maximizar variedad en sugerencias
        val COMMON_SPANISH_WORDS = listOf(
            "amor","dolor","calor","flor","vapor","cantor","señor","ardor",
            "temblor","clamor","rumor","fulgor","esplendor","verdor","sopor",
            "vida","herida","florida","querida","sonrisa","brisa","cima","rima",
            "alma","palma","calma","balsa","salsa","danza","lanza","esperanza",
            "luna","bruna","fortuna","tribuna","laguna","ninguna","alguna",
            "noche","coche","broche","derroche","reproche",
            "cielo","vuelo","suelo","anhelo","consuelo","duelo","hielo","velo",
            "mar","amar","cantar","soñar","llorar","bailar","volar","mirar",
            "viento","tormento","momento","pensamiento","lamento","acento",
            "corazón","canción","ilusión","emoción","pasión","razón","traición",
            "tiempo","cuerpo","sueño","leño","dueño","empeño","sello",
            "tierra","guerra","sierra","piedra","sombra","nombra",
            "fuego","juego","ruego","entrego","ciego","luego",
            "agua","fragua","pájaros","estrellas","bellas","ellas","ellos",
            "manos","lejanos","hermanos","villanos","granos","planos","urbanos",
            "tarde","alarde","cobarde","baluarte",
            "verde","puede","suede","cede","sede","mede",
            "sangre","hambre","cumbre","lumbre","nombre","hombre","sobre",
            "río","vacío","frío","navío","poderío",
            "luz","cruz","capuz","altavoz","atroz","feroz","veloz","precoz",
            "paz","haz","faz","disfraz","compás","jamás","además","detrás",
            "ser","querer","poder","saber","tener","ver","volver","beber",
            "día","alegría","energía","melancolía","poesía","armonía","valentía",
            "sol","col","rol","caracol","farol","arrebol",
            "fuente","gente","mente","frente","valiente","ardiente","silente",
            "rosa","mariposa","hermosa","dichosa","gloriosa","nebulosa",
            "pena","serena","cadena","arena","escena","llena","colmena",
            "voz","feroz","veloz","atroz","precoz",
            "cielo","anhelo","vuelo","duelo","suelo","hielo",
            "bien","también","también","amén","sostén","rehén","sartén"
        ).distinct()
    }
}
