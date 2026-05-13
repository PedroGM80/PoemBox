package dev.pgm.poembox.presentation.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pgm.poembox.domain.PoemUtils
import dev.pgm.poembox.domain.UserSessionManager
import dev.pgm.poembox.domain.UtilitySyllables
import dev.pgm.poembox.domain.model.Draft
import dev.pgm.poembox.domain.usecase.SaveDraftUseCase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    private val saveDraftUseCase: SaveDraftUseCase,
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

    private val poemUtils = PoemUtils()
    private val utilitySyllables = UtilitySyllables()

    fun onTitleChange(newTitle: String) {
        if (newTitle.length <= 60) {
            _title.value = newTitle
            _isSaved.value = false
        }
    }

    fun onContentChange(newContent: String) {
        _content.value = newContent
        _isSaved.value = false
        analyzeContent(newContent)
    }

    fun clearPoem() {
        _title.value = ""
        _content.value = ""
        _analysisResult.value = ""
        _isSaved.value = false
    }

    private fun analyzeContent(text: String) {
        if (text.isBlank()) {
            _analysisResult.value = ""
            return
        }
        val lines = text.split("\n")
        val lastLine = lines.lastOrNull { it.isNotBlank() } ?: ""
        if (lastLine.isNotBlank()) {
            val syllables = utilitySyllables.getSyllables(lastLine)
            val isAcute = poemUtils.isAcute(lastLine.split(" ").last())
            val isProparoxytone = poemUtils.isProparoxytone(lastLine.split(" ").last())
            val countSinhalese = poemUtils.hasSinhalese(lastLine)
            val total = syllables.size + isAcute + isProparoxytone + countSinhalese
            _analysisResult.value = "Último verso: $total sílabas"
        }
    }

    fun saveDraft(userName: String, onSuccess: () -> Unit) {
        if (_title.value.isBlank()) return
        viewModelScope.launch {
            val draft = Draft(
                title = _title.value,
                content = _content.value,
                author = userName,
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
