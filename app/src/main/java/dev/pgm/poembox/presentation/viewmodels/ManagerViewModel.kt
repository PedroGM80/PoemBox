package dev.pgm.poembox.presentation.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pgm.poembox.domain.usecase.DeletePoemUseCase
import dev.pgm.poembox.domain.usecase.GetAllSheetsUseCase
import dev.pgm.poembox.domain.usecase.GetDraftByTitleUseCase
import dev.pgm.poembox.presentation.content.PoemDetails
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManagerViewModel @Inject constructor(
    private val getAllSheetsUseCase: GetAllSheetsUseCase,
    private val getDraftByTitleUseCase: GetDraftByTitleUseCase,
    private val deletePoemUseCase: DeletePoemUseCase
) : ViewModel() {

    private val _poems = mutableStateOf<List<PoemDetails>>(emptyList())
    val poems: State<List<PoemDetails>> = _poems

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    fun loadPoems() {
        viewModelScope.launch {
            _isLoading.value = true
            val sheets = getAllSheetsUseCase()
            val detailsList = mutableListOf<PoemDetails>()
            sheets.forEach { sheet ->
                val draft = getDraftByTitleUseCase(sheet.draftTitle)
                if (draft != null) {
                    detailsList.add(
                        PoemDetails(
                            title = draft.title,
                            author = draft.author,
                            date = sheet.validationDate,
                            annotations = draft.annotation,
                            poem = draft.content
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
            deletePoemUseCase(poem.title, poem.date)
            loadPoems()
        }
    }
}
