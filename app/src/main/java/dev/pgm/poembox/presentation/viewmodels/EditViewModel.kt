package dev.pgm.poembox.presentation.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pgm.poembox.data.Draft
import dev.pgm.poembox.domain.PoemRepository
import dev.pgm.poembox.domain.PoemUtils
import dev.pgm.poembox.domain.UtilitySyllables
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    private val repository: PoemRepository
) : ViewModel() {

    private val _title = mutableStateOf("")
    val title: State<String> = _title

    private val _content = mutableStateOf("")
    val content: State<String> = _content

    private val _analysisResult = mutableStateOf("")
    val analysisResult: State<String> = _analysisResult

    private val poemUtils = PoemUtils()
    private val utilitySyllables = UtilitySyllables()

    fun onTitleChange(newTitle: String) {
        if (newTitle.length <= 60) {
            _title.value = newTitle
        }
    }

    fun onContentChange(newContent: String) {
        _content.value = newContent
        analyzeContent(newContent)
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
            _analysisResult.value = "Last line: $total syllables"
        }
    }

    fun saveDraft(userName: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val draft = Draft(
                title = _title.value,
                draftContent = _content.value,
                writerName = userName,
                draftAnnotation = "",
                writtenDate = getDate()
            )
            repository.insertDraft(draft)
            onSuccess()
        }
    }

    private fun getDate(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return formatter.format(Date())
    }
}
