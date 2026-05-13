package dev.pgm.poembox.presentation.viewmodels

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.pgm.poembox.R
import dev.pgm.poembox.domain.PoemUtils
import dev.pgm.poembox.domain.UserSessionManager
import dev.pgm.poembox.domain.UtilitySyllables
import dev.pgm.poembox.domain.model.Draft
import dev.pgm.poembox.domain.usecase.GetDraftByTitleUseCase
import dev.pgm.poembox.domain.usecase.SaveDraftUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private val sessionManager: UserSessionManager
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

    private val _lineSyllables = mutableStateOf<List<Pair<String, Int>>>(emptyList())
    val lineSyllables: State<List<Pair<String, Int>>> = _lineSyllables

    private val poemUtils = PoemUtils()
    private val utilitySyllables = UtilitySyllables()

    // Debounce análisis: no ejecuta en cada pulsación sino 250ms después de parar de escribir
    private val _analysisInput = MutableStateFlow("")

    init {
        viewModelScope.launch {
            _analysisInput
                .debounce(250L)
                .filter { it.isNotBlank() }
                .collect { text ->
                    val (result, lines) = withContext(Dispatchers.Default) {
                        computeAnalysis(text) to computeLineSyllables(text)
                    }
                    _analysisResult.value = result
                    _lineSyllables.value = lines
                }
        }
        viewModelScope.launch {
            sessionManager.pendingEditTitle.filter { it.isNotBlank() }.collect { title ->
                val draft = getDraftByTitleUseCase(title)
                draft?.let {
                    _title.value = it.title
                    _content.value = it.content
                    _annotation.value = it.annotation
                    _isSaved.value = true
                    _wordCount.value = if (it.content.isBlank()) 0
                                       else it.content.trim().split(Regex("\\s+")).size
                    _analysisInput.value = it.content
                }
                sessionManager.consumeEditRequest()
            }
        }
    }

    fun onTitleChange(newTitle: String) {
        if (newTitle.length <= 60) {
            _title.value = newTitle
            _isSaved.value = false
        }
    }

    fun onContentChange(newContent: String) {
        _content.value = newContent
        _isSaved.value = false
        _wordCount.value = if (newContent.isBlank()) 0 else newContent.trim().split(Regex("\\s+")).size
        if (newContent.isBlank()) {
            _analysisResult.value = ""
        } else {
            _analysisInput.value = newContent
        }
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
        _lineSyllables.value = emptyList()
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

    private fun computeLineSyllables(text: String): List<Pair<String, Int>> {
        return text.split("\n").filter { it.isNotBlank() }.map { line ->
            val syllables = utilitySyllables.getSyllables(line)
            val lastWord = line.trim().split(" ").last()
            val isAcute = poemUtils.isAcute(lastWord)
            val isProparoxytone = poemUtils.isProparoxytone(lastWord)
            val countSinhalese = poemUtils.hasSinhalese(line)
            line to (syllables.size + isAcute + isProparoxytone + countSinhalese)
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
            _isSaved.value = true
            onSuccess()
        }
    }

    private fun getDate(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return formatter.format(Date())
    }
}
