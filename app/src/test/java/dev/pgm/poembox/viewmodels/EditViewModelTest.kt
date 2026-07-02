package dev.pgm.poembox.viewmodels

import android.content.Context
import dev.pgm.poembox.R
import dev.pgm.poembox.domain.PoemUtils
import dev.pgm.poembox.domain.UtilitySyllables
import dev.pgm.poembox.domain.model.Draft
import dev.pgm.poembox.domain.usecase.GetDraftByTitleUseCase
import dev.pgm.poembox.domain.usecase.SaveDraftUseCase
import dev.pgm.poembox.presentation.viewmodels.EditViewModel
import dev.pgm.poembox.util.FakeSessionManager
import dev.pgm.poembox.util.MainDispatcherRule
import dev.pgm.poembox.worker.DailyReminderScheduler
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.Runs
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [EditViewModel].
 *
 * Real [UtilitySyllables] and [PoemUtils] run actual syllable analysis.
 * [DailyReminderScheduler] is mocked relaxed — WorkManager not available in unit tests.
 * [PoemBoxWidgetUpdater.update()] wraps in runCatching so failures are swallowed silently.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class EditViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val context: Context = mockk(relaxed = true)
    private val saveDraftUseCase: SaveDraftUseCase = mockk()
    private val getDraftByTitleUseCase: GetDraftByTitleUseCase = mockk()
    private val fakeSession = FakeSessionManager()
    private val dailyReminderScheduler: DailyReminderScheduler = mockk(relaxed = true)
    private val utilitySyllables = UtilitySyllables()
    private val poemUtils = PoemUtils(utilitySyllables)

    private lateinit var viewModel: EditViewModel

    @Before
    fun setUp() {
        every { context.getString(R.string.analysis_last_line_syllables, any()) } returns "8 silabas"
        coEvery { saveDraftUseCase(any()) } just Runs

        viewModel = EditViewModel(
            context = context,
            saveDraftUseCase = saveDraftUseCase,
            getDraftByTitleUseCase = getDraftByTitleUseCase,
            sessionManager = fakeSession,
            dailyReminderScheduler = dailyReminderScheduler,
            poemUtils = poemUtils,
            utilitySyllables = utilitySyllables
        )
    }

    @After
    fun tearDown() {
        // Cancel viewModelScope before MainDispatcherRule resets Dispatchers.Main.
        // Prevents the withContext(Dispatchers.Default) analysis coroutine from
        // trying to resume on a Main dispatcher that no longer exists.
        viewModel.viewModelScope.cancel()
    }

    // ── initial state ─────────────────────────────────────────────────────────

    @Test
    fun `initial title is blank`() {
        assertEquals("", viewModel.title.value)
    }

    // ── onTitleChange ─────────────────────────────────────────────────────────

    @Test
    fun `onTitleChange updates title`() {
        viewModel.onTitleChange("Mi poema")
        assertEquals("Mi poema", viewModel.title.value)
    }

    // ── onContentChange ───────────────────────────────────────────────────────

    @Test
    fun `onContentChange updates content`() {
        viewModel.onContentChange("Verso 1\nVerso 2")
        assertTrue(viewModel.content.value.contains("Verso 1"))
        assertTrue(viewModel.content.value.contains("Verso 2"))
    }

    // ── saveDraft ─────────────────────────────────────────────────────────────

    @Test
    fun `saveDraft with blank title does nothing`() = runTest {
        // title starts blank — saveDraft should be a no-op
        viewModel.saveDraft {}
        advanceUntilIdle()

        coVerify(exactly = 0) { saveDraftUseCase(any()) }
    }

    @Test
    fun `saveDraft success sets isSaved true`() = runTest {
        viewModel.onTitleChange("Mi poema")
        viewModel.onContentChange("Verso 1\nVerso 2")
        viewModel.onAuthorChange("Pedro")

        viewModel.saveDraft {}
        advanceUntilIdle()

        assertTrue("isSaved should be true after successful save", viewModel.isSaved.value)
    }

    @Test
    fun `saveDraft calls saveDraftUseCase once`() = runTest {
        viewModel.onTitleChange("Soneto")
        viewModel.onContentChange("Catorce versos")
        viewModel.onAuthorChange("Neruda")

        viewModel.saveDraft {}
        advanceUntilIdle()

        coVerify(exactly = 1) { saveDraftUseCase(any()) }
    }

    @Test
    fun `saveDraft calls sessionManager setCurrentPoemTitle`() = runTest {
        viewModel.onTitleChange("Cancion")
        viewModel.onContentChange("Letra")

        viewModel.saveDraft {}
        advanceUntilIdle()

        assertEquals("Cancion", fakeSession._currentPoemTitle.value)
    }

    // ── isSaved resets ────────────────────────────────────────────────────────

    @Test
    fun `isSaved resets to false on new content`() = runTest {
        viewModel.onTitleChange("Poema")
        viewModel.onContentChange("Contenido")

        viewModel.saveDraft {}
        advanceUntilIdle()

        assertTrue("isSaved should be true after save", viewModel.isSaved.value)

        viewModel.onContentChange("Nuevo contenido")

        assertFalse("isSaved should reset to false when content changes", viewModel.isSaved.value)
    }
}
