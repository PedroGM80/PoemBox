package dev.pgm.poembox.presentation.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pgm.poembox.data.Draft
import dev.pgm.poembox.data.Sheet
import dev.pgm.poembox.domain.PoemRepository
import dev.pgm.poembox.presentation.content.PoemDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ManagerViewModel @Inject constructor(
    private val repository: PoemRepository
) : ViewModel() {

    private val _poems = mutableStateOf<List<PoemDetails>>(emptyList())
    val poems: State<List<PoemDetails>> = _poems

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    fun loadPoems() {
        viewModelScope.launch {
            _isLoading.value = true
            val sheets = withContext(Dispatchers.IO) {
                repository.getAllSheet()
            }
            val detailsList = mutableListOf<PoemDetails>()
            sheets?.forEach { sheet ->
                val draft = withContext(Dispatchers.IO) {
                    repository.findDraftByTitle(sheet.refDraftValidate)
                }
                if (draft != null) {
                    detailsList.add(
                        PoemDetails(
                            title = draft.title,
                            author = draft.writerName,
                            date = sheet.dateValidation,
                            annotations = draft.draftAnnotation,
                            poem = draft.draftContent
                        )
                    )
                }
            }
            _poems.value = detailsList
            _isLoading.value = false
        }
    }

    fun deletePoem(poem: PoemDetails) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val sheet = repository.findSheetByDateCreation(poem.date)
                val draft = repository.findDraftByTitle(poem.title)
                if (sheet != null) repository.deleteSheet(sheet)
                if (draft != null) repository.deleteDraft(draft)
            }
            loadPoems() // Reload after delete
        }
    }
    
    fun updateAnnotations(title: String, annotations: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.updateNoteByTitle(annotations, title)
            }
            loadPoems()
        }
    }
}
