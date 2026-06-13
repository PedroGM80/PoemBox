package dev.pgm.poembox.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import dev.pgm.poembox.data.PoemBoxDatabase
import dev.pgm.poembox.domain.SessionManager
import dev.pgm.poembox.domain.StreakCalculator
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

@Singleton
class UserSessionManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: PoemBoxDatabase
) : SessionManager {
    private companion object {
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val CURRENT_POEM_TITLE = stringPreferencesKey("current_poem_title")
        val DAILY_REMINDER_ENABLED = booleanPreferencesKey("daily_reminder_enabled")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val STREAK_CURRENT = intPreferencesKey("streak_current")
        val STREAK_MAX = intPreferencesKey("streak_max")
        val STREAK_LAST_DATE = stringPreferencesKey("streak_last_date")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    }

    override val userName: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[USER_NAME]
    }

    override val currentPoemTitle: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[CURRENT_POEM_TITLE] ?: ""
    }

    override val dailyReminderEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[DAILY_REMINDER_ENABLED] ?: false
    }

    override val themeMode: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[THEME_MODE] ?: "LIGHT"
    }

    override suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { prefs -> prefs[THEME_MODE] = mode }
    }

    override suspend fun setDailyReminderEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[DAILY_REMINDER_ENABLED] = enabled }
    }

    override suspend fun saveUser(name: String, email: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_NAME] = name
            prefs[USER_EMAIL] = email
        }
    }

    override suspend fun setCurrentPoemTitle(title: String) {
        context.dataStore.edit { prefs ->
            prefs[CURRENT_POEM_TITLE] = title
        }
    }

    override suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
        database.clearAllTables()
    }

    override val onboardingCompleted: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[ONBOARDING_COMPLETED] ?: false
    }

    override suspend fun setOnboardingCompleted() {
        context.dataStore.edit { prefs -> prefs[ONBOARDING_COMPLETED] = true }
    }

    override val streak: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[STREAK_CURRENT] ?: 0
    }

    override val maxStreak: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[STREAK_MAX] ?: 0
    }

    override suspend fun recordWriteToday() {
        val today = fmt.format(Date())
        val cal = Calendar.getInstance().also { it.add(Calendar.DAY_OF_YEAR, -1) }
        val yesterday = fmt.format(cal.time)
        context.dataStore.edit { prefs ->
            val current = prefs[STREAK_CURRENT] ?: 0
            val newStreak = StreakCalculator.compute(prefs[STREAK_LAST_DATE], today, yesterday, current)
            if (newStreak == current && prefs[STREAK_LAST_DATE] == today) return@edit  // ya contado hoy
            prefs[STREAK_CURRENT] = newStreak
            prefs[STREAK_MAX] = StreakCalculator.computeMaxStreak(prefs[STREAK_MAX] ?: 0, newStreak)
            prefs[STREAK_LAST_DATE] = today
        }
    }

    private val _pendingEditTitle = MutableStateFlow("")
    override val pendingEditTitle: StateFlow<String> = _pendingEditTitle.asStateFlow()

    override fun requestEditPoem(title: String) { _pendingEditTitle.value = title }
    override fun consumeEditRequest() { _pendingEditTitle.value = "" }
}
