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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SortOrder { DATE_DESC, DATE_ASC, TITLE_ASC }

@HiltViewModel
class ManagerViewModel @Inject constructor(
    private val getAllSheetsUseCase: GetAllSheetsUseCase,
    private val getDraftByTitleUseCase: GetDraftByTitleUseCase,
    private val deletePoemUseCase: DeletePoemUseCase
) : ViewModel() {

    private val _allPoems = mutableStateOf<List<PoemDetails>>(emptyList())

    private val _poems = mutableStateOf<List<PoemDetails>>(emptyList())
    val poems: State<List<PoemDetails>> = _poems

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    private val _sortOrder = mutableStateOf(SortOrder.DATE_DESC)
    val sortOrder: State<SortOrder> = _sortOrder

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        applyFilter()
    }

    fun onSortOrderChange(order: SortOrder) {
        _sortOrder.value = order
        applyFilter()
    }

    private fun applyFilter() {
        val q = _searchQuery.value.trim().lowercase()
        val filtered = if (q.isBlank()) _allPoems.value
        else _allPoems.value.filter {
            it.title.lowercase().contains(q) || it.author.lowercase().contains(q)
        }
        _poems.value = when (_sortOrder.value) {
            SortOrder.DATE_DESC -> filtered.sortedByDescending { it.date }
            SortOrder.DATE_ASC -> filtered.sortedBy { it.date }
            SortOrder.TITLE_ASC -> filtered.sortedBy { it.title.lowercase() }
        }
    }

    fun loadPoems() {
        viewModelScope.launch {
            _isLoading.value = true
            val sheets = getAllSheetsUseCase()
            val detailsList = sheets.map { sheet ->
                async {
                    val draft = getDraftByTitleUseCase(sheet.draftTitle)
                    if (draft != null) {
                        PoemDetails(
                            title = draft.title,
                            author = draft.author,
                            date = sheet.validationDate,
                            annotations = draft.annotation,
                            poem = draft.content
                        )
                    } else null
                }
            }.awaitAll().filterNotNull()
            _allPoems.value = detailsList
            applyFilter()
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
