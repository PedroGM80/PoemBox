package dev.pgm.poembox.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface SessionManager {
    val userName: Flow<String?>
    val currentPoemTitle: Flow<String>
    val dailyReminderEnabled: Flow<Boolean>
    val geminiApiKey: Flow<String>
    val pendingEditTitle: StateFlow<String>

    suspend fun setDailyReminderEnabled(enabled: Boolean)
    suspend fun setGeminiApiKey(key: String)
    suspend fun saveUser(name: String, email: String)
    suspend fun setCurrentPoemTitle(title: String)
    suspend fun clearSession()
    fun requestEditPoem(title: String)
    fun consumeEditRequest()
}
