package dev.pgm.poembox.viewmodels

import dev.pgm.poembox.domain.model.Draft
import dev.pgm.poembox.domain.model.Sheet
import dev.pgm.poembox.domain.usecase.GetAllDraftsUseCase
import dev.pgm.poembox.domain.usecase.GetAllSheetsUseCase
import dev.pgm.poembox.presentation.viewmodels.PoetStats
import dev.pgm.poembox.presentation.viewmodels.StatsViewModel
import dev.pgm.poembox.util.FakeSessionManager
import dev.pgm.poembox.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [StatsViewModel].
 *
 * Strategy:
 *  - Use mockk to fake [GetAllDraftsUseCase] and [GetAllSheetsUseCase].
 *  - Use [FakeSessionManager] (hand-written fake) for the session layer.
 *  - [MainDispatcherRule] replaces Dispatchers.Main with an unconfined dispatcher
 *    so viewModelScope.launch runs synchronously in test context.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class StatsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getAllDraftsUseCase: GetAllDraftsUseCase = mockk()
    private val getAllSheetsUseCase: GetAllSheetsUseCase = mockk()
    private val fakeSession = FakeSessionManager()

    private lateinit var viewModel: StatsViewModel

    @Before
    fun setUp() {
        viewModel = StatsViewModel(getAllDraftsUseCase, getAllSheetsUseCase, fakeSession)
    }

    // ── Initial state ─────────────────────────────────────────────────────────

    @Test
    fun `initial stats are all zeros and blank title`() {
        val initial = viewModel.stats.value
        assertEquals(PoetStats(), initial)
        assertEquals(0, initial.totalDrafts)
        assertEquals(0, initial.totalWords)
        assertEquals(0, initial.validatedPoems)
        assertEquals(0, initial.longestPoemWords)
        assertEquals("", initial.longestPoemTitle)
        assertEquals(0, initial.currentStreak)
        assertEquals(0, initial.maxStreak)
    }

    // ── load() with empty collections ─────────────────────────────────────────

    @Test
    fun `load with empty drafts sets totalDrafts to 0 and totalWords to 0`() = runTest {
        coEvery { getAllDraftsUseCase() } returns emptyList()
        coEvery { getAllSheetsUseCase() } returns emptyList()

        viewModel.load()
        advanceUntilIdle()

        val stats = viewModel.stats.value
        assertEquals(0, stats.totalDrafts)
        assertEquals(0, stats.totalWords)
    }

    @Test
    fun `load with empty sheets sets validatedPoems to 0`() = runTest {
        coEvery { getAllDraftsUseCase() } returns emptyList()
        coEvery { getAllSheetsUseCase() } returns emptyList()

        viewModel.load()
        advanceUntilIdle()

        assertEquals(0, viewModel.stats.value.validatedPoems)
    }

    @Test
    fun `longestPoemTitle is blank when no drafts`() = runTest {
        coEvery { getAllDraftsUseCase() } returns emptyList()
        coEvery { getAllSheetsUseCase() } returns emptyList()

        viewModel.load()
        advanceUntilIdle()

        assertTrue(viewModel.stats.value.longestPoemTitle.isBlank())
    }

    // ── load() with real drafts ───────────────────────────────────────────────

    @Test
    fun `load with 3 drafts sets totalDrafts to 3`() = runTest {
        coEvery { getAllDraftsUseCase() } returns listOf(
            Draft(title = "A", content = "one two three", author = ""),
            Draft(title = "B", content = "four five", author = ""),
            Draft(title = "C", content = "six", author = "")
        )
        coEvery { getAllSheetsUseCase() } returns emptyList()

        viewModel.load()
        advanceUntilIdle()

        assertEquals(3, viewModel.stats.value.totalDrafts)
    }

    @Test
    fun `load with 3 drafts sums total word count correctly`() = runTest {
        // "one two three"=3, "four five"=2, "six"=1 → total=6
        coEvery { getAllDraftsUseCase() } returns listOf(
            Draft(title = "A", content = "one two three", author = ""),
            Draft(title = "B", content = "four five", author = ""),
            Draft(title = "C", content = "six", author = "")
        )
        coEvery { getAllSheetsUseCase() } returns emptyList()

        viewModel.load()
        advanceUntilIdle()

        assertEquals(6, viewModel.stats.value.totalWords)
    }

    @Test
    fun `load identifies longest poem by word count`() = runTest {
        coEvery { getAllDraftsUseCase() } returns listOf(
            Draft(title = "Short", content = "one two", author = ""),
            Draft(title = "Medium", content = "alpha beta gamma delta", author = ""),
            Draft(title = "Long", content = "a b c d e f g h i j", author = "")
        )
        coEvery { getAllSheetsUseCase() } returns emptyList()

        viewModel.load()
        advanceUntilIdle()

        val stats = viewModel.stats.value
        assertEquals("Long", stats.longestPoemTitle)
        assertEquals(10, stats.longestPoemWords)
    }

    @Test
    fun `load with blank content draft counts as 0 words`() = runTest {
        coEvery { getAllDraftsUseCase() } returns listOf(
            Draft(title = "Empty", content = "   ", author = ""),
            Draft(title = "Real", content = "word", author = "")
        )
        coEvery { getAllSheetsUseCase() } returns emptyList()

        viewModel.load()
        advanceUntilIdle()

        // blank content → 0 words, "word" → 1 word, total = 1
        assertEquals(1, viewModel.stats.value.totalWords)
        assertEquals("Real", viewModel.stats.value.longestPoemTitle)
    }

    // ── load() with sheets ─────────────────────────────────────────────────────

    @Test
    fun `load counts validated poems from sheets`() = runTest {
        coEvery { getAllDraftsUseCase() } returns emptyList()
        coEvery { getAllSheetsUseCase() } returns listOf(
            Sheet(draftTitle = "Poem1", validationDate = "2026-01-01"),
            Sheet(draftTitle = "Poem2", validationDate = "2026-01-02")
        )

        viewModel.load()
        advanceUntilIdle()

        assertEquals(2, viewModel.stats.value.validatedPoems)
    }

    // ── load() with session streak data ──────────────────────────────────────

    @Test
    fun `load picks up streak from session manager`() = runTest {
        fakeSession._streak.value = 5
        fakeSession._maxStreak.value = 10

        coEvery { getAllDraftsUseCase() } returns emptyList()
        coEvery { getAllSheetsUseCase() } returns emptyList()

        viewModel.load()
        advanceUntilIdle()

        val stats = viewModel.stats.value
        assertEquals(5, stats.currentStreak)
        assertEquals(10, stats.maxStreak)
    }

    @Test
    fun `load with zero streak stays at 0`() = runTest {
        fakeSession._streak.value = 0
        fakeSession._maxStreak.value = 0

        coEvery { getAllDraftsUseCase() } returns emptyList()
        coEvery { getAllSheetsUseCase() } returns emptyList()

        viewModel.load()
        advanceUntilIdle()

        assertEquals(0, viewModel.stats.value.currentStreak)
        assertEquals(0, viewModel.stats.value.maxStreak)
    }
}
