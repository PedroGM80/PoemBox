package dev.pgm.poembox.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.pgm.poembox.R
import dev.pgm.poembox.domain.SessionManager
import dev.pgm.poembox.domain.model.PoeticFormDef
import dev.pgm.poembox.domain.model.PoeticForms
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GeminiUiState(
    val suggestion: String = "",
    val isLoading: Boolean = false,
    val error: String = ""
)

@HiltViewModel
class GeminiViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(GeminiUiState())
    val state: StateFlow<GeminiUiState> = _state.asStateFlow()

    val apiKey: StateFlow<String> = sessionManager.geminiApiKey
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    fun saveApiKey(key: String) {
        viewModelScope.launch { sessionManager.setGeminiApiKey(key.trim()) }
    }

    fun suggestNextVerse(poemText: String, form: PoeticFormDef, currentKey: String) {
        if (currentKey.isBlank()) return
        _state.value = GeminiUiState(isLoading = true)
        viewModelScope.launch {
            try {
                val model = GenerativeModel(modelName = "gemini-2.0-flash", apiKey = currentKey)
                val formHint = buildFormHint(form)
                val prompt = buildString {
                    append("Eres un poeta experto en español. ")
                    append("Dado el siguiente poema incompleto:\n\n")
                    append(poemText)
                    append("\n\n")
                    if (formHint.isNotBlank()) append("$formHint\n\n")
                    append("Sugiere ÚNICAMENTE el siguiente verso que continúe el poema de forma natural, ")
                    append("respetando el ritmo, el tema y el tono. ")
                    append("Responde con una sola línea de verso, sin explicaciones ni comillas.")
                }
                val response = model.generateContent(prompt)
                val suggestion = response.text?.trim() ?: ""
                _state.value = GeminiUiState(suggestion = suggestion, isLoading = false)
            } catch (e: Exception) {
                _state.value = GeminiUiState(
                    error = context.getString(R.string.gemini_error, e.message ?: "Unknown error")
                )
            }
        }
    }

    fun clearSuggestion() {
        _state.value = GeminiUiState()
    }

    private fun buildFormHint(form: PoeticFormDef): String {
        if (form.isFree) return ""
        val syllables = form.syllablesPerLine.firstOrNull()
        return when {
            syllables != null && form.rhymeScheme.isNotEmpty() ->
                "La forma es ${form.id} — cada verso debe tener $syllables sílabas y respetar el esquema de rima."
            syllables != null ->
                "La forma es ${form.id} — cada verso debe tener $syllables sílabas."
            else -> "La forma es ${form.id}."
        }
    }
}
