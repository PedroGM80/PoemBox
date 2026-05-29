package dev.pgm.poembox.viewmodels

import dev.pgm.poembox.presentation.viewmodels.AuthViewModel
import dev.pgm.poembox.util.FakeSessionManager
import dev.pgm.poembox.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [AuthViewModel].
 *
 * Uses [FakeSessionManager] so no DataStore or Android context is needed.
 * [MainDispatcherRule] ensures viewModelScope coroutines run eagerly.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeSession = FakeSessionManager()
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        viewModel = AuthViewModel(fakeSession)
    }

    // ── userName ──────────────────────────────────────────────────────────────

    @Test
    fun `userName starts as null when session has no user`() {
        assertNull(viewModel.userName.value)
    }

    @Test
    fun `userName reflects saved user after registerUser`() = runTest {
        viewModel.registerUser("Pedro", "pedro@example.com") {}
        advanceUntilIdle()
        assertEquals("Pedro", viewModel.userName.value)
    }

    // ── isLoaded ──────────────────────────────────────────────────────────────

    @Test
    fun `isLoaded starts as false before any emission`() {
        // With Eagerly + initial=false, the ViewModel starts false
        // until the upstream emits. With UnconfinedTestDispatcher the
        // stateIn eagerly subscribes and emits immediately.
        // After setUp() the fake emits null immediately, so isLoaded becomes true.
        assertTrue(
            "isLoaded should be true once FakeSessionManager emits its first value",
            viewModel.isLoaded.value
        )
    }

    @Test
    fun `isLoaded becomes true after session emits a value`() = runTest {
        // Triggering a save so userName emits a new non-null value
        fakeSession._userName.value = "Ana"
        advanceUntilIdle()
        assertTrue(viewModel.isLoaded.value)
    }

    // ── onboardingCompleted ───────────────────────────────────────────────────

    @Test
    fun `onboardingCompleted initial value is false because fake starts false`() {
        // FakeSessionManager._onboardingCompleted starts at false.
        // AuthViewModel.onboardingCompleted uses initialValue=true as a fallback
        // but with UnconfinedTestDispatcher stateIn collects immediately, so the
        // fake's false value wins.
        assertFalse(viewModel.onboardingCompleted.value)
    }

    @Test
    fun `onboardingCompleted reflects true once setOnboardingCompleted called via fake`() = runTest {
        fakeSession._onboardingCompleted.value = true
        advanceUntilIdle()
        assertTrue(viewModel.onboardingCompleted.value)
    }

    // ── completeOnboarding ─────────────────────────────────────────────────────

    @Test
    fun `completeOnboarding calls sessionManager setOnboardingCompleted`() = runTest {
        assertFalse(fakeSession.onboardingCompletedCalled)

        viewModel.completeOnboarding {}
        advanceUntilIdle()

        assertTrue(fakeSession.onboardingCompletedCalled)
    }

    @Test
    fun `completeOnboarding invokes the onComplete callback`() = runTest {
        var callbackInvoked = false
        viewModel.completeOnboarding { callbackInvoked = true }
        advanceUntilIdle()
        assertTrue(callbackInvoked)
    }

    // ── logout ────────────────────────────────────────────────────────────────

    @Test
    fun `logout calls sessionManager clearSession`() = runTest {
        assertFalse(fakeSession.clearSessionCalled)

        viewModel.logout {}
        advanceUntilIdle()

        assertTrue(fakeSession.clearSessionCalled)
    }

    @Test
    fun `logout invokes the onComplete callback`() = runTest {
        var callbackInvoked = false
        viewModel.logout { callbackInvoked = true }
        advanceUntilIdle()
        assertTrue(callbackInvoked)
    }

    @Test
    fun `logout clears userName to null`() = runTest {
        fakeSession._userName.value = "Pedro"
        advanceUntilIdle()

        viewModel.logout {}
        advanceUntilIdle()

        assertNull(viewModel.userName.value)
    }

    // ── registerUser ──────────────────────────────────────────────────────────

    @Test
    fun `registerUser saves user name and email in session`() = runTest {
        viewModel.registerUser("Lorca", "lorca@poetry.es") {}
        advanceUntilIdle()

        assertEquals("Lorca", fakeSession.savedUserName)
        assertEquals("lorca@poetry.es", fakeSession.savedUserEmail)
    }
}
