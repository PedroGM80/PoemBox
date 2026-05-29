package dev.pgm.poembox.util

import dev.pgm.poembox.domain.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * In-memory fake of [SessionManager] for unit tests.
 * All Flows are backed by MutableStateFlow so tests can set values directly
 * without any DataStore or Android runtime dependency.
 */
class FakeSessionManager : SessionManager {

    val _userName = MutableStateFlow<String?>(null)
    override val userName: Flow<String?> = _userName

    val _currentPoemTitle = MutableStateFlow("")
    override val currentPoemTitle: Flow<String> = _currentPoemTitle

    val _dailyReminderEnabled = MutableStateFlow(false)
    override val dailyReminderEnabled: Flow<Boolean> = _dailyReminderEnabled

    val _themeMode = MutableStateFlow("system")
    override val themeMode: Flow<String> = _themeMode

    val _pendingEditTitle = MutableStateFlow("")
    override val pendingEditTitle: StateFlow<String> = _pendingEditTitle

    val _onboardingCompleted = MutableStateFlow(false)
    override val onboardingCompleted: Flow<Boolean> = _onboardingCompleted

    val _streak = MutableStateFlow(0)
    override val streak: Flow<Int> = _streak

    val _maxStreak = MutableStateFlow(0)
    override val maxStreak: Flow<Int> = _maxStreak

    // Tracking vars for verifying calls
    var onboardingCompletedCalled = false
    var clearSessionCalled = false
    var savedUserName: String? = null
    var savedUserEmail: String? = null

    override suspend fun setThemeMode(mode: String) { _themeMode.value = mode }
    override suspend fun setDailyReminderEnabled(enabled: Boolean) { _dailyReminderEnabled.value = enabled }
    override suspend fun saveUser(name: String, email: String) {
        savedUserName = name
        savedUserEmail = email
        _userName.value = name
    }
    override suspend fun setCurrentPoemTitle(title: String) { _currentPoemTitle.value = title }
    override suspend fun clearSession() {
        clearSessionCalled = true
        _userName.value = null
    }
    override fun requestEditPoem(title: String) { _pendingEditTitle.value = title }
    override fun consumeEditRequest() { _pendingEditTitle.value = "" }
    override suspend fun setOnboardingCompleted() {
        onboardingCompletedCalled = true
        _onboardingCompleted.value = true
    }
    override suspend fun recordWriteToday() {
        _streak.value += 1
    }
}
