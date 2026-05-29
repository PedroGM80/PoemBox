package dev.pgm.poembox.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pgm.poembox.domain.SessionManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    val userName: StateFlow<String?> = sessionManager.userName.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    /** True once DataStore has emitted its first value (distinguishes "loading" from "not registered"). */
    val isLoaded: StateFlow<Boolean> = sessionManager.userName
        .take(1)
        .map { true }
        .stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = false)

    val onboardingCompleted: StateFlow<Boolean> = sessionManager.onboardingCompleted
        .stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = true)

    fun registerUser(name: String, email: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            sessionManager.saveUser(name, email)
            onComplete()
        }
    }

    fun completeOnboarding(onComplete: () -> Unit) {
        viewModelScope.launch {
            sessionManager.setOnboardingCompleted()
            onComplete()
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            sessionManager.clearSession()
            onComplete()
        }
    }
}
