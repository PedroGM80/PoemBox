package dev.pgm.poembox.presentation.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pgm.poembox.domain.PoemUtils
import dev.pgm.poembox.domain.UtilitySyllables
import dev.pgm.poembox.domain.model.Sheet
import dev.pgm.poembox.domain.usecase.GetDraftByTitleUseCase
import dev.pgm.poembox.domain.usecase.SaveSheetUseCase
import dev.pgm.poembox.presentation.MainActivity
import kotlinx.coroutines.Dispatchers
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
    private val getDraftByTitleUseCase: GetDraftByTitleUseCase,
    private val saveSheetUseCase: SaveSheetUseCase
) : ViewModel() {

    private val _state = mutableStateOf(MonitoringState())
    val state: State<MonitoringState> = _state

    private val poemUtils = PoemUtils()
    private val utilitySyllables = UtilitySyllables()

    fun loadPoem() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val titleToLoad = MainActivity.POEM_TITLE
            if (titleToLoad.isNotBlank()) {
                val draft = getDraftByTitleUseCase(titleToLoad)
                if (draft != null) {
                    _state.value = _state.value.copy(
                        title = draft.title,
                        body = draft.content,
                        isLoading = false
                    )
                    analyzePoem(draft.content)
                }
            }
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
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:MM:SS", Locale.getDefault())
        return formatter.format(Date())
    }

    private fun analyzePoem(content: String) {
        if (content.isBlank()) return

        val schema = "Analysis complete" 
        val predominate = getPredominateNumberSyllables(content)
        val syllablesAnalysis = "$predominate syllable verses predominate."

        val numberStanza = poemUtils.getNumberStanza(content)
        val numberVerse = poemUtils.getNumberOfVerse(content)
        val versesPerStanza = if (numberStanza > 0) numberVerse / numberStanza else 0
        val versesAnalysis = "The poem is made up of $numberStanza stanzas, $numberVerse verses ($versesPerStanza per stanza)."

        val rhymeAnalysis = getRhymeType(content)
        val enjambment = poemUtils.getEnjambment(content)

        _state.value = _state.value.copy(
            syllablesAnalysis = syllablesAnalysis,
            versesAnalysis = versesAnalysis,
            rhymeAnalysis = rhymeAnalysis,
            enjambmentAnalysis = enjambment
        )
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
            uniqueAssonant < uniqueConsonant -> "Rhyme is Assonance"
            uniqueAssonant > uniqueConsonant -> "Rhyme is Consonance"
            else -> "Rhyme is Undefined/Mixed"
        }
    }

    private fun getPredominateNumberSyllables(content: String): String {
        val lines = content.split("\n").filter { it.isNotBlank() }
        val counts = lines.map { line ->
            val clean = line.replace(Regex("[.,;:]"), "")
            val syllables = utilitySyllables.getSyllables(clean)
            val isAcute = poemUtils.isAcute(clean.split(" ").last())
            val isProparoxytone = poemUtils.isProparoxytone(clean.split(" ").last())
            val countSinhalese = poemUtils.hasSinhalese(clean)
            syllables.size + isAcute + isProparoxytone + countSinhalese
        }
        return counts.groupBy { it }.maxByOrNull { it.value.size }?.key?.toString() ?: "0"
    }
}
