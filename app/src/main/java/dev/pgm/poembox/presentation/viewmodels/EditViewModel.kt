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
import dev.pgm.poembox.worker.DailyReminderScheduler
import dev.pgm.poembox.domain.model.Draft
import dev.pgm.poembox.domain.model.LineValidation
import dev.pgm.poembox.domain.model.PoeticFormDef
import dev.pgm.poembox.domain.model.PoeticForms
import dev.pgm.poembox.domain.usecase.GetDraftByTitleUseCase
import dev.pgm.poembox.domain.usecase.SaveDraftUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import dev.pgm.poembox.presentation.widget.PoemBoxWidgetUpdater
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class EditViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val saveDraftUseCase: SaveDraftUseCase,
    private val getDraftByTitleUseCase: GetDraftByTitleUseCase,
    private val sessionManager: SessionManager,
    private val dailyReminderScheduler: DailyReminderScheduler,
    private val poemUtils: PoemUtils,
    private val utilitySyllables: UtilitySyllables
) : ViewModel() {

    private val _title = mutableStateOf("")
    val title: State<String> = _title

    private val _content = mutableStateOf("")
    val content: State<String> = _content

    private val _analysisResult = mutableStateOf("")
    val analysisResult: State<String> = _analysisResult

    private val _isSaved = mutableStateOf(false)
    val isSaved: State<Boolean> = _isSaved

    private val _wordCount = mutableStateOf(0)
    val wordCount: State<Int> = _wordCount

    private val _annotation = mutableStateOf("")
    val annotation: State<String> = _annotation

    private val _selectedForm = mutableStateOf<PoeticFormDef>(PoeticForms.LIBRE)
    val selectedForm: State<PoeticFormDef> = _selectedForm

    private val _lineValidations = mutableStateOf<List<LineValidation>>(emptyList())
    val lineValidations: State<List<LineValidation>> = _lineValidations

    val dailyReminderEnabled = sessionManager.dailyReminderEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun setDailyReminder(enabled: Boolean) {
        viewModelScope.launch {
            sessionManager.setDailyReminderEnabled(enabled)
            if (enabled) dailyReminderScheduler.schedule() else dailyReminderScheduler.cancel()
        }
    }

    // Debounce análisis: no ejecuta en cada pulsación sino 250ms después de parar de escribir
    private val _analysisInput = MutableStateFlow("")

    init {
        viewModelScope.launch {
            _analysisInput
                .debounce(250L)
                .filter { it.isNotBlank() }
                .collect { text ->
                    val (result, validations) = withContext(Dispatchers.Default) {
                        computeAnalysis(text) to computeLineValidations(text, _selectedForm.value)
                    }
                    _analysisResult.value = result
                    _lineValidations.value = validations
                }
        }
        viewModelScope.launch {
            sessionManager.pendingEditTitle.filter { it.isNotBlank() }.collectLatest { title ->
                try {
                    val draft = getDraftByTitleUseCase(title)
                    draft?.let {
                        _title.value = it.title
                        _content.value = it.content
                        _annotation.value = it.annotation
                        _isSaved.value = true
                        _wordCount.value = if (it.content.isBlank()) 0
                                           else it.content.trim().split(Regex(Constants.REGEX_WHITESPACE)).size
                        _analysisInput.value = it.content
                    }
                } finally {
                    sessionManager.consumeEditRequest()
                }
            }
        }
    }

    fun onTitleChange(newTitle: String) {
        if (newTitle.length <= Constants.MAX_TITLE_LENGTH) {
            _title.value = newTitle
            _isSaved.value = false
        }
    }

    fun onContentChange(newContent: String) {
        _content.value = newContent
        _isSaved.value = false
        _wordCount.value = if (newContent.isBlank()) 0 else newContent.trim().split(Regex(Constants.REGEX_WHITESPACE)).size
        if (newContent.isBlank()) {
            _analysisResult.value = ""
            _lineValidations.value = emptyList()
        } else {
            _analysisInput.value = newContent
        }
    }

    fun onFormSelected(form: PoeticFormDef) {
        _selectedForm.value = form
        if (_content.value.isNotBlank()) _analysisInput.value = _content.value
    }

    fun onAnnotationChange(newAnnotation: String) {
        _annotation.value = newAnnotation
        _isSaved.value = false
    }

    fun clearPoem() {
        _title.value = ""
        _content.value = ""
        _analysisResult.value = ""
        _annotation.value = ""
        _lineValidations.value = emptyList()
        _isSaved.value = false
        _wordCount.value = 0
        _analysisInput.value = ""
    }

    private fun computeAnalysis(text: String): String {
        val lines = text.split("\n")
        val lastLine = lines.lastOrNull { it.isNotBlank() } ?: return ""
        val syllables = utilitySyllables.getSyllables(lastLine)
        val lastWord = lastLine.trim().split(" ").last()
        val isAcute = poemUtils.isAcute(lastWord)
        val isProparoxytone = poemUtils.isProparoxytone(lastWord)
        val countSinhalese = poemUtils.hasSinhalese(lastLine)
        val total = syllables.size + isAcute + isProparoxytone + countSinhalese
        return context.getString(R.string.analysis_last_line_syllables, total)
    }

    private fun computeLineValidations(text: String, form: PoeticFormDef): List<LineValidation> {
        val lines = text.split("\n").filter { it.isNotBlank() }

        val basic = lines.mapIndexed { index, line ->
            val syllables = utilitySyllables.getSyllables(line)
            val lastWord = line.trim().split(" ").lastOrNull() ?: ""
            val isAcute = poemUtils.isAcute(lastWord)
            val isProparoxytone = poemUtils.isProparoxytone(lastWord)
            val countSinhalese = poemUtils.hasSinhalese(line)
            val actual = syllables.size + isAcute + isProparoxytone + countSinhalese
            val expected = form.getSyllablesForLine(index)
            LineValidation(
                index = index,
                lineText = line,
                actualSyllables = actual,
                expectedSyllables = expected,
                rhymeLetter = form.getRhymeLetterForLine(index),
                syllableOk = expected == null || actual == expected
            )
        }

        if (form.rhymeScheme.isEmpty()) return basic

        // Build map: rhymeLetter → list of (lineText, ending) for lines already written
        val letterToEndings: Map<Char, List<Pair<Int, String>>> = basic
            .filter { it.rhymeLetter != null && it.lineText.isNotBlank() }
            .groupBy { it.rhymeLetter!! }
            .mapValues { (_, group) ->
                group.map { v ->
                    val lastWord = v.lineText.trim().split(" ").lastOrNull() ?: ""
                    v.index to utilitySyllables.getLastSyllable(lastWord)
                }
            }

        return basic.map { v ->
            val letter = v.rhymeLetter ?: return@map v
            val endings = letterToEndings[letter] ?: return@map v
            val others = endings.filter { it.first != v.index }
            if (others.isEmpty()) return@map v  // first line with this letter, pending

            val referenceEnding = others.first().second
            val myLastWord = v.lineText.trim().split(" ").lastOrNull() ?: ""
            val myEnding = utilitySyllables.getLastSyllable(myLastWord)
            val myVowel = utilitySyllables.getLastVowel(myLastWord)
            val refVowel = utilitySyllables.getLastVowel(
                others.first().let { lines.getOrNull(it.first)?.trim()?.split(" ")?.lastOrNull() ?: "" }
            )

            v.copy(
                rhymesOk = myEnding == referenceEnding || (myVowel.isNotEmpty() && myVowel == refVowel),
                rhymeHint = if (referenceEnding.isNotEmpty()) "-$referenceEnding" else null
            )
        }
    }

    fun saveDraft(userName: String, onSuccess: () -> Unit) {
        if (_title.value.isBlank()) return
        viewModelScope.launch {
            val draft = Draft(
                title = _title.value,
                content = _content.value,
                author = userName,
                annotation = _annotation.value,
                date = getDate()
            )
            saveDraftUseCase(draft)
            sessionManager.setCurrentPoemTitle(_title.value)
            sessionManager.recordWriteToday()
            val streak = sessionManager.streak.first()
            PoemBoxWidgetUpdater.update(context, _title.value, streak)
            _isSaved.value = true
            onSuccess()
        }
    }

    private fun getDate(): String {
        val formatter = SimpleDateFormat(Constants.DATE_FORMAT_FULL, Locale.getDefault())
        return formatter.format(Date())
    }
}
