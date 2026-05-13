package dev.pgm.poembox.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pgm.poembox.domain.usecase.GetAllDraftsUseCase
import dev.pgm.poembox.domain.usecase.GetAllSheetsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PoetStats(
    val totalDrafts: Int = 0,
    val validatedPoems: Int = 0,
    val totalWords: Int = 0,
    val longestPoemWords: Int = 0,
    val longestPoemTitle: String = ""
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val getAllDraftsUseCase: GetAllDraftsUseCase,
    private val getAllSheetsUseCase: GetAllSheetsUseCase
) : ViewModel() {

    private val _stats = MutableStateFlow(PoetStats())
    val stats: StateFlow<PoetStats> = _stats.asStateFlow()

    fun load() {
        viewModelScope.launch {
            val drafts = getAllDraftsUseCase()
            val sheets = getAllSheetsUseCase()
            val longest = drafts.maxByOrNull { it.content.split(Regex("\\s+")).size }
            _stats.value = PoetStats(
                totalDrafts = drafts.size,
                validatedPoems = sheets.size,
                totalWords = drafts.sumOf { d ->
                    if (d.content.isBlank()) 0 else d.content.trim().split(Regex("\\s+")).size
                },
                longestPoemWords = longest?.let {
                    if (it.content.isBlank()) 0 else it.content.trim().split(Regex("\\s+")).size
                } ?: 0,
                longestPoemTitle = longest?.title ?: ""
            )
        }
    }
}
