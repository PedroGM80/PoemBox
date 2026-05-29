package dev.pgm.poembox.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface SessionManager {
    val userName: Flow<String?>
    val currentPoemTitle: Flow<String>
    val dailyReminderEnabled: Flow<Boolean>
    val themeMode: Flow<String>
    val pendingEditTitle: StateFlow<String>
    /** True si el usuario ya ha visto el onboarding al menos una vez. */
    val onboardingCompleted: Flow<Boolean>
    /** Días consecutivos en que el usuario ha guardado al menos un borrador. */
    val streak: Flow<Int>
    /** Mejor racha histórica. */
    val maxStreak: Flow<Int>

    suspend fun setThemeMode(mode: String)
    suspend fun setDailyReminderEnabled(enabled: Boolean)
    suspend fun saveUser(name: String, email: String)
    suspend fun setCurrentPoemTitle(title: String)
    suspend fun clearSession()
    fun requestEditPoem(title: String)
    fun consumeEditRequest()
    /** Marca el onboarding como completado para no mostrarlo de nuevo. */
    suspend fun setOnboardingCompleted()
    /** Registra que el usuario ha escrito hoy y actualiza la racha. */
    suspend fun recordWriteToday()
}
