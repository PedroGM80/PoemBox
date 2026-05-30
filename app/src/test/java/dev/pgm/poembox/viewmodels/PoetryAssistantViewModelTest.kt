package dev.pgm.poembox.viewmodels

import android.app.ActivityManager
import android.content.Context
import dev.pgm.poembox.domain.UtilitySyllables
import dev.pgm.poembox.presentation.ai.DeviceAILevel
import dev.pgm.poembox.presentation.ai.PoetryAssistantViewModel
import dev.pgm.poembox.presentation.ai.RhymeSuggester
import dev.pgm.poembox.util.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [PoetryAssistantViewModel].
 *
 * Context is mocked to report 2 GB RAM so [DeviceAICapability.maxLevel] returns
 * LEVEL_RULES (below the 4 GB threshold required for LEVEL_LLM_INFERENCE).
 *
 * Real [RhymeSuggester] and [UtilitySyllables] run the actual phonetic analysis
 * to exercise the full pipeline without mocking domain utilities.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PoetryAssistantViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val context: Context = mockk(relaxed = true)
    private val utilitySyllables = UtilitySyllables()
    private val rhymeSuggester = RhymeSuggester(utilitySyllables)

    private lateinit var viewModel: PoetryAssistantViewModel

    @Before
    fun setUp() {
        // Stub ActivityManager to report 2 GB total RAM → LEVEL_RULES
        val activityManager = mockk<ActivityManager>(relaxed = true)
        every { context.getSystemService(Context.ACTIVITY_SERVICE) } returns activityManager
        every { activityManager.getMemoryInfo(any()) } answers {
            val info = firstArg<ActivityManager.MemoryInfo>()
            info.totalMem = 2L * 1024 * 1024 * 1024 // 2 GB — below 4 GB threshold
        }

        viewModel = PoetryAssistantViewModel(
            context = context,
            rhymeSuggester = rhymeSuggester
        ).also { it.computeDispatcher = UnconfinedTestDispatcher() }
    }

    // ── init / AI level ───────────────────────────────────────────────────────

    @Test
    fun `initial aiLevel is LEVEL_RULES on low RAM device`() {
        assertEquals(DeviceAILevel.LEVEL_RULES, viewModel.state.value.aiLevel)
    }

    @Test
    fun `initial levelLabel is not blank`() {
        assertTrue(
            "levelLabel should not be blank",
            viewModel.state.value.levelLabel.isNotBlank()
        )
    }

    // ── analyzeRhyme ──────────────────────────────────────────────────────────

    @Test
    fun `analyzeRhyme blank verse clears analysis`() = runTest {
        viewModel.analyzeRhyme("")
        advanceUntilIdle()

        assertNull("rhymeAnalysis should be null for blank verse", viewModel.state.value.rhymeAnalysis)
    }

    @Test
    fun `analyzeRhyme real verse populates analysis`() = runTest {
        viewModel.analyzeRhyme("Verde que te quiero verde")
        advanceUntilIdle()

        assertNotNull(
            "rhymeAnalysis should not be null for a real verse",
            viewModel.state.value.rhymeAnalysis
        )
    }

    @Test
    fun `analyzeRhyme sets lastWord correctly`() = runTest {
        viewModel.analyzeRhyme("caminante")
        advanceUntilIdle()

        val analysis = viewModel.state.value.rhymeAnalysis
        assertNotNull("analysis should not be null", analysis)
        assertEquals("caminante", analysis!!.lastWord)
    }

    // ── clearRhyme ────────────────────────────────────────────────────────────

    @Test
    fun `clearRhyme sets analysis null after analyzeRhyme`() = runTest {
        viewModel.analyzeRhyme("amor")
        advanceUntilIdle()
        assertNotNull("analysis should be set before clear", viewModel.state.value.rhymeAnalysis)

        viewModel.clearRhyme()
        advanceUntilIdle()

        assertNull("rhymeAnalysis should be null after clearRhyme()", viewModel.state.value.rhymeAnalysis)
    }
}
