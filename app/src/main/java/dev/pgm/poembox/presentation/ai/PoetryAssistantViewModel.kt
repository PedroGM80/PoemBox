package dev.pgm.poembox.presentation.ai

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class AssistantState(
    val aiLevel: DeviceAILevel = DeviceAILevel.LEVEL_RULES,
    val levelLabel: String = "",
    val upgradeHint: String? = null,
    val rhymeAnalysis: RhymeSuggester.RhymeAnalysis? = null,
    val llmResponse: String = "",
    val isLoadingLlm: Boolean = false,
    val llmAvailable: Boolean = false,   // modelo LLM descargado y listo
    val llmError: String? = null
)

@HiltViewModel
class PoetryAssistantViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val rhymeSuggester: RhymeSuggester
) : ViewModel() {

    /** Sobrescribible en tests: viewModel.computeDispatcher = UnconfinedTestDispatcher() */
    internal var computeDispatcher: CoroutineDispatcher = Dispatchers.Default

    private val _state = MutableStateFlow(AssistantState())
    val state: StateFlow<AssistantState> = _state.asStateFlow()

    init {
        val level = DeviceAICapability.maxLevel(context)
        _state.value = _state.value.copy(
            aiLevel = level,
            levelLabel = DeviceAICapability.levelLabel(level),
            upgradeHint = DeviceAICapability.upgradeHint(level)
        )
    }

    // ── Nivel 1: sugerencias de rima (siempre disponible) ─────────────────

    fun analyzeRhyme(verse: String) {
        viewModelScope.launch(computeDispatcher) {
            val analysis = runCatching { rhymeSuggester.analyze(verse) }.getOrNull()
            _state.value = _state.value.copy(rhymeAnalysis = analysis)
        }
    }

    fun clearRhyme() {
        _state.value = _state.value.copy(rhymeAnalysis = null)
    }

    // ── Nivel 2: LLM Inference (≥4 GB RAM, Android 10+) ──────────────────
    // El modelo (~1.5 GB) se descarga la primera vez mediante la UI de descarga.
    // Si el modelo no está disponible, llmAvailable = false.

    fun askLlm(prompt: String, onResult: (String) -> Unit) {
        if (!DeviceAICapability.isLlmInferenceCapable(context)) return
        _state.value = _state.value.copy(isLoadingLlm = true, llmError = null)
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching { invokeLlmInference(prompt) }.getOrElse { e ->
                    FirebaseCrashlytics.getInstance().recordException(e)
                    null
                }
            }
            if (result != null) {
                _state.value = _state.value.copy(
                    llmResponse = result,
                    isLoadingLlm = false
                )
                onResult(result)
            } else {
                _state.value = _state.value.copy(
                    isLoadingLlm = false,
                    llmError = "El modelo no está disponible. Descárgalo desde ajustes."
                )
            }
        }
    }

    /**
     * Invoca MediaPipe LLM Inference si el modelo está presente.
     * Devuelve null si el modelo no está cargado.
     *
     * El modelo recomendado es Gemma-2B-IT-Q8 (~2 GB) o Phi-2-Q8 (~1.5 GB),
     * descargable desde Kaggle o desde la UI de la app.
     * Ruta esperada: /data/local/tmp/llm/model.bin (configurable).
     */
    private fun invokeLlmInference(prompt: String): String? {
        return try {
            val options = com.google.mediapipe.tasks.genai.llminference.LlmInference.LlmInferenceOptions
                .builder()
                .setModelPath(LLM_MODEL_PATH)
                .setMaxTokens(256)
                .build()
            com.google.mediapipe.tasks.genai.llminference.LlmInference
                .createFromOptions(context, options)
                .use { llm -> llm.generateResponse(prompt) }
        } catch (_: Exception) {
            null  // modelo no disponible
        }
    }

    companion object {
        /** Ruta del modelo LLM en el dispositivo (el usuario lo descarga). */
        const val LLM_MODEL_PATH = "/data/local/tmp/llm/model.bin"
    }
}
