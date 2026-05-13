package dev.pgm.poembox.presentation.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pgm.poembox.domain.UserSessionManager
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ScreenTabsViewModel @Inject constructor(
    sessionManager: UserSessionManager
) : ViewModel() {
    val pendingEditTitle: StateFlow<String> = sessionManager.pendingEditTitle
}
