package dev.pgm.poembox.domain

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyllableEngineFactory @Inject constructor(
    private val spanishEngine: SpanishSyllableEngine,
    private val englishEngine: EnglishSyllableEngine
) {
    fun getEngine(languageCode: String): SyllableEngine {
        return when (languageCode.lowercase()) {
            "en" -> englishEngine
            else -> spanishEngine
        }
    }
}
