package dev.pgm.poembox.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pgm.poembox.domain.SessionManager
import dev.pgm.poembox.presentation.theme.PoemBoxThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    val themeMode: StateFlow<PoemBoxThemeMode> = sessionManager.themeMode
        .map { mode ->
            try {
                PoemBoxThemeMode.valueOf(mode)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                PoemBoxThemeMode.LIGHT
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PoemBoxThemeMode.LIGHT
        )

    fun setThemeMode(mode: PoemBoxThemeMode) {
        viewModelScope.launch {
            sessionManager.setThemeMode(mode.name)
        }
    }
}
