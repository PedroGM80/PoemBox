package dev.pgm.poembox.domain

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

@Singleton
class UserSessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val CURRENT_POEM_TITLE = stringPreferencesKey("current_poem_title")
        val DAILY_REMINDER_ENABLED = booleanPreferencesKey("daily_reminder_enabled")
        val GEMINI_API_KEY = stringPreferencesKey("gemini_api_key")
    }

    val userName: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[USER_NAME]
    }

    val currentPoemTitle: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[CURRENT_POEM_TITLE] ?: ""
    }

    val dailyReminderEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[DAILY_REMINDER_ENABLED] ?: false
    }

    val geminiApiKey: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[GEMINI_API_KEY] ?: ""
    }

    suspend fun setDailyReminderEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[DAILY_REMINDER_ENABLED] = enabled }
    }

    suspend fun setGeminiApiKey(key: String) {
        context.dataStore.edit { prefs -> prefs[GEMINI_API_KEY] = key }
    }

    suspend fun saveUser(name: String, email: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_NAME] = name
            prefs[USER_EMAIL] = email
        }
    }

    suspend fun setCurrentPoemTitle(title: String) {
        context.dataStore.edit { prefs ->
            prefs[CURRENT_POEM_TITLE] = title
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }

    private val _pendingEditTitle = MutableStateFlow("")
    val pendingEditTitle: StateFlow<String> = _pendingEditTitle.asStateFlow()

    fun requestEditPoem(title: String) { _pendingEditTitle.value = title }
    fun consumeEditRequest() { _pendingEditTitle.value = "" }
}
