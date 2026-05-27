package dev.pgm.poembox.presentation.viewmodels

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.pgm.poembox.R
import dev.pgm.poembox.domain.Constants
import dev.pgm.poembox.domain.PoemUtils
import dev.pgm.poembox.domain.SessionManager
import dev.pgm.poembox.domain.UtilitySyllables
import dev.pgm.poembox.domain.model.Sheet
import dev.pgm.poembox.domain.usecase.GetDraftByTitleUseCase
import dev.pgm.poembox.domain.usecase.SaveSheetUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class MonitoringState(
    val title: String = "",
    val body: String = "",
    val syllablesAnalysis: String = "",
    val versesAnalysis: String = "",
    val rhymeAnalysis: String = "",
    val enjambmentAnalysis: String = "",
    val isLoading: Boolean = false,
    val isValidated: Boolean = false
)

@HiltViewModel
class MonitoringViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getDraftByTitleUseCase: GetDraftByTitleUseCase,
    private val saveSheetUseCase: SaveSheetUseCase,
    private val sessionManager: SessionManager,
    private val poemUtils: PoemUtils,
    private val utilitySyllables: UtilitySyllables
) : ViewModel() {

    private val _state = mutableStateOf(MonitoringState())
    val state: State<MonitoringState> = _state

    init {
        viewModelScope.launch {
            sessionManager.currentPoemTitle.collect { title ->
                if (title.isNotBlank()) loadPoemByTitle(title)
            }
        }
    }

    fun loadPoem() {
        viewModelScope.launch {
            val title = sessionManager.currentPoemTitle.first()
            if (title.isNotBlank()) loadPoemByTitle(title)
        }
    }

    private suspend fun loadPoemByTitle(title: String) {
        _state.value = _state.value.copy(isLoading = true, isValidated = false)
        val draft = getDraftByTitleUseCase(title)
        if (draft != null) {
            // Análisis pesado en hilo CPU, no en Main
            val analysis = withContext(Dispatchers.Default) {
                computeAnalysis(draft.content)
            }
            _state.value = _state.value.copy(
                title = draft.title,
                body = draft.content,
                syllablesAnalysis = analysis.syllables,
                versesAnalysis = analysis.verses,
                rhymeAnalysis = analysis.rhyme,
                enjambmentAnalysis = analysis.enjambment,
                isLoading = false
            )
        } else {
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    fun validatePoem() {
        viewModelScope.launch {
            val sheet = Sheet(
                draftTitle = _state.value.title,
                validationDate = getDate()
            )
            saveSheetUseCase(sheet)
            _state.value = _state.value.copy(isValidated = true)
        }
    }

    private fun getDate(): String {
        val formatter = SimpleDateFormat(Constants.DATE_FORMAT_FULL, Locale.getDefault())
        return formatter.format(Date())
    }

    private data class AnalysisResult(
        val syllables: String,
        val verses: String,
        val rhyme: String,
        val enjambment: String
    )

    private fun computeAnalysis(content: String): AnalysisResult {
        if (content.isBlank()) return AnalysisResult("", "", "", "")

        val predominate = getPredominateNumberSyllables(content)
        val syllablesText = context.getString(R.string.analysis_syllable_predominant, predominate)

        val numberStanza = poemUtils.getNumberStanza(content)
        val numberVerse = poemUtils.getNumberOfVerse(content)
        val versesPerStanza = if (numberStanza > 0) numberVerse / numberStanza else 0
        val versesText = context.getString(R.string.analysis_structure, numberStanza, numberVerse, versesPerStanza)

        val rhymeText = getRhymeType(content)
        val enjambmentText = poemUtils.getEnjambment(content)

        return AnalysisResult(syllablesText, versesText, rhymeText, enjambmentText)
    }

    private fun getRhymeType(content: String): String {
        val lines = content.split("\n").filter { it.isNotBlank() }
        val consonantRime = mutableMapOf<String, String>()
        val assonantRime = mutableMapOf<String, String>()

        lines.forEachIndexed { index, line ->
            val lastWord = line.trim().split(" ").lastOrNull() ?: ""
            if (lastWord.isNotBlank()) {
                consonantRime[index.toString()] = utilitySyllables.getLastSyllable(lastWord)
                assonantRime[index.toString()] = utilitySyllables.getLastVowel(lastWord)
            }
        }

        val uniqueAssonant = assonantRime.values.distinct().size
        val uniqueConsonant = consonantRime.values.distinct().size

        return when {
            uniqueAssonant < uniqueConsonant -> context.getString(R.string.rhyme_assonant)
            uniqueAssonant > uniqueConsonant -> context.getString(R.string.rhyme_consonant)
            else -> context.getString(R.string.rhyme_mixed)
        }
    }

    private fun getPredominateNumberSyllables(content: String): String {
        val lines = content.split("\n").filter { it.isNotBlank() }
        val counts = lines.map { line ->
            val clean = line.replace(Regex(Constants.REGEX_PUNCTUATION), "")
            val syllables = utilitySyllables.getSyllables(clean)
            val lastWord = clean.trim().split(" ").last()
            val isAcute = poemUtils.isAcute(lastWord)
            val isProparoxytone = poemUtils.isProparoxytone(lastWord)
            val countSinhalese = poemUtils.hasSinhalese(clean)
            syllables.size + isAcute + isProparoxytone + countSinhalese
        }
        return counts.groupBy { it }.maxByOrNull { it.value.size }?.key?.toString() ?: Constants.SYLLABLES_DEFAULT
    }
}
