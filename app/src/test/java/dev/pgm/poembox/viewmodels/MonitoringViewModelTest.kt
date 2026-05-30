package dev.pgm.poembox.viewmodels

import android.content.Context
import dev.pgm.poembox.R
import dev.pgm.poembox.domain.PoemUtils
import dev.pgm.poembox.domain.UtilitySyllables
import dev.pgm.poembox.domain.model.Draft
import dev.pgm.poembox.domain.usecase.GetDraftByTitleUseCase
import dev.pgm.poembox.domain.usecase.SaveSheetUseCase
import dev.pgm.poembox.presentation.viewmodels.MonitoringViewModel
import dev.pgm.poembox.util.FakeSessionManager
import dev.pgm.poembox.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [MonitoringViewModel].
 *
 * Real [UtilitySyllables] and [PoemUtils] are used so the syllable/verse analysis
 * logic is exercised end-to-end without mocking domain utilities.
 *
 * [Context] is mocked to avoid Android framework calls from getString().
 * [FakeSessionManager] provides a DataStore-free session layer.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MonitoringViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val context: Context = mockk(relaxed = true)
    private val getDraftByTitleUseCase: GetDraftByTitleUseCase = mockk()
    private val saveSheetUseCase: SaveSheetUseCase = mockk(relaxed = true)
    private val fakeSession = FakeSessionManager()
    private val utilitySyllables = UtilitySyllables()
    private val poemUtils = PoemUtils(utilitySyllables)

    private lateinit var viewModel: MonitoringViewModel

    @Before
    fun setUp() {
        stubContextStrings()
        viewModel = MonitoringViewModel(
            context = context,
            getDraftByTitleUseCase = getDraftByTitleUseCase,
            saveSheetUseCase = saveSheetUseCase,
            sessionManager = fakeSession,
            poemUtils = poemUtils,
            utilitySyllables = utilitySyllables
        ).also { it.computeDispatcher = UnconfinedTestDispatcher() }
    }

    private fun stubContextStrings() {
        every { context.getString(R.string.analysis_syllable_predominant, any()) } returns
                "Predominan los versos de 8 silabas."
        every { context.getString(R.string.analysis_structure, any(), any(), any()) } returns
                "El poema tiene 1 estrofas y 4 versos (4 por estrofa)."
        every { context.getString(R.string.rhyme_assonant) } returns "Rima asonante"
        every { context.getString(R.string.rhyme_consonant) } returns "Rima consonante"
        every { context.getString(R.string.rhyme_mixed) } returns "Rima indefinida / mixta"
    }

    // ── initial state ─────────────────────────────────────────────────────────

    @Test
    fun `initial state is empty`() {
        val state = viewModel.state.value
        assertTrue("title should be blank initially", state.title.isBlank())
        assertTrue("body should be blank initially", state.body.isBlank())
        assertTrue("syllablesAnalysis should be blank initially", state.syllablesAnalysis.isBlank())
        assertFalse("isLoading should be false initially", state.isLoading)
    }

    // ── loadPoem ──────────────────────────────────────────────────────────────

    @Test
    fun `load poem populates state`() = runTest {
        val poem = "Verde que te quiero verde\nverde viento verdes ramas"
        coEvery { getDraftByTitleUseCase(any()) } returns
                Draft(title = "Verde", content = poem, author = "Lorca")

        fakeSession._currentPoemTitle.value = "Verde"
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue("title should be set", state.title == "Verde")
        assertTrue("body should be set", state.body == poem)
    }

    @Test
    fun `load poem with blank title does nothing`() = runTest {
        // currentPoemTitle stays "" (FakeSessionManager default) — loadPoem should be a no-op
        viewModel.loadPoem()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue("title should remain blank", state.title.isBlank())
        assertTrue("body should remain blank", state.body.isBlank())
    }

    @Test
    fun `getDraftByTitleUseCase returns null leaves state empty`() = runTest {
        coEvery { getDraftByTitleUseCase(any()) } returns null

        fakeSession._currentPoemTitle.value = "Inexistente"
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue("title should remain blank when draft not found", state.title.isBlank())
        assertFalse("isLoading should be false after null result", state.isLoading)
    }

    // ── validatePoem ──────────────────────────────────────────────────────────

    @Test
    fun `validate poem sets isValidated true`() = runTest {
        viewModel.validatePoem()
        advanceUntilIdle()

        assertTrue("isValidated should be true after validatePoem()", viewModel.state.value.isValidated)
    }

    @Test
    fun `validate poem calls saveSheetUseCase`() = runTest {
        viewModel.validatePoem()
        advanceUntilIdle()

        coVerify(exactly = 1) { saveSheetUseCase(any()) }
    }

    // ── analysis with real content ─────────────────────────────────────────────

    @Test
    fun `syllablesAnalysis is not blank after loading poem`() = runTest {
        val poem = "Verde que te quiero verde\nverde viento verdes ramas"
        coEvery { getDraftByTitleUseCase(any()) } returns
                Draft(title = "Verde", content = poem, author = "Lorca")

        fakeSession._currentPoemTitle.value = "Verde"
        advanceUntilIdle()

        assertTrue(
            "syllablesAnalysis should not be blank after loading a real poem",
            viewModel.state.value.syllablesAnalysis.isNotBlank()
        )
    }

    @Test
    fun `versesAnalysis is not blank after loading poem`() = runTest {
        val poem = "Verde que te quiero verde\nverde viento verdes ramas"
        coEvery { getDraftByTitleUseCase(any()) } returns
                Draft(title = "Verde", content = poem, author = "Lorca")

        fakeSession._currentPoemTitle.value = "Verde"
        advanceUntilIdle()

        assertTrue(
            "versesAnalysis should not be blank after loading a real poem",
            viewModel.state.value.versesAnalysis.isNotBlank()
        )
    }

    @Test
    fun `computeAnalysis blank content returns empty strings`() = runTest {
        coEvery { getDraftByTitleUseCase(any()) } returns
                Draft(title = "Vacio", content = "   ", author = "Autor")

        fakeSession._currentPoemTitle.value = "Vacio"
        advanceUntilIdle()

        val state = viewModel.state.value
        // After loading a blank-content draft, analysis fields should be blank
        assertTrue(
            "syllablesAnalysis should be blank for blank content",
            state.syllablesAnalysis.isBlank()
        )
    }
}
