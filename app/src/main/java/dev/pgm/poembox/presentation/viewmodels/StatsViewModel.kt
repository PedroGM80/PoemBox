package dev.pgm.poembox.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pgm.poembox.domain.Constants
import dev.pgm.poembox.domain.SessionManager
import dev.pgm.poembox.domain.usecase.GetAllDraftsUseCase
import dev.pgm.poembox.domain.usecase.GetAllSheetsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PoetStats(
    val totalDrafts: Int = 0,
    val validatedPoems: Int = 0,
    val totalWords: Int = 0,
    val longestPoemWords: Int = 0,
    val longestPoemTitle: String = "",
    val currentStreak: Int = 0,
    val maxStreak: Int = 0
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val getAllDraftsUseCase: GetAllDraftsUseCase,
    private val getAllSheetsUseCase: GetAllSheetsUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _stats = MutableStateFlow(PoetStats())
    val stats: StateFlow<PoetStats> = _stats.asStateFlow()

    fun load() {
        viewModelScope.launch {
            val drafts = getAllDraftsUseCase()
            val sheets = getAllSheetsUseCase()
            val wordCounts = drafts.map { d ->
                if (d.content.isBlank()) 0 else d.content.trim().split(Regex(Constants.REGEX_WHITESPACE)).size
            }
            val longestIndex = wordCounts.indices.maxByOrNull { wordCounts[it] }
            _stats.value = PoetStats(
                totalDrafts = drafts.size,
                validatedPoems = sheets.size,
                totalWords = wordCounts.sum(),
                longestPoemWords = longestIndex?.let { wordCounts[it] } ?: 0,
                longestPoemTitle = longestIndex?.let { drafts[it].title } ?: "",
                currentStreak = sessionManager.streak.first(),
                maxStreak = sessionManager.maxStreak.first()
            )
        }
    }
}
