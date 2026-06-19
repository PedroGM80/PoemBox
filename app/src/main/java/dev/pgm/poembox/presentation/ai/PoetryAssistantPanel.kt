package dev.pgm.poembox.presentation.ai

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pgm.poembox.presentation.theme.Dimens

/**
 * Panel del asistente poético integrado en el editor.
 *
 * Muestra siempre el nivel de IA disponible y las sugerencias de rima (Nivel 1).
 * Si el dispositivo soporta LLM Inference (Nivel 2) o Gemini Nano (Nivel 3),
 * aparece la sección de generación de texto con el botón correspondiente.
 * Si el nivel superior no está disponible, se muestra una explicación clara.
 *
 * @param currentVerse El verso actual del editor (última línea escrita).
 * @param onInsertWord Callback que inserta una palabra sugerida en el editor.
 */
@Composable
fun PoetryAssistantPanel(
    currentVerse: String,
    onInsertWord: (String) -> Unit,
    viewModel: PoetryAssistantViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    // Analiza el verso actual cuando cambia
    LaunchedEffect(currentVerse) {
        if (currentVerse.isNotBlank()) viewModel.analyzeRhyme(currentVerse)
        else viewModel.clearRhyme()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.SpacingSmall),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(Dimens.PaddingLarge)) {
            // Header — siempre visible
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(Dimens.PaddingSmall))
                Text(
                    text = state.levelLabel,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { expanded = !expanded }, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(Modifier.height(Dimens.PaddingMedium))

                    // ── Nivel 1: sugerencias de rima ───────────────────────
                    val analysis = state.rhymeAnalysis
                    if (analysis != null) {
                        Text(
                            text = "Rima de «${analysis.lastWord}»: ${analysis.consonantPattern}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "Rima asonante en: ${analysis.asonantPattern}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                        )
                        if (analysis.suggestions.isNotEmpty()) {
                            Spacer(Modifier.height(Dimens.PaddingSmall))
                            Text(
                                text = "Palabras que riman:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(Modifier.height(4.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)) {
                                items(analysis.suggestions) { word ->
                                    SuggestionChip(
                                        onClick = { onInsertWord(word) },
                                        label = { Text(word, fontSize = 12.sp) }
                                    )
                                }
                            }
                        }
                    } else if (currentVerse.isBlank()) {
                        Text(
                            text = "Escribe un verso para ver sugerencias de rima.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                        )
                    }

                    // La continuación de verso por LLM se ofrecerá cuando la
                    // app incluya un modelo on-device descargable. Por ahora el
                    // asistente se limita a las sugerencias de rima (arriba),
                    // que funcionan en todos los dispositivos y sin conexión.
                    // LlmSection() se conserva más abajo para esa futura versión.
                }
            }
        }
    }
}

@Composable
private fun LlmSection(
    state: AssistantState,
    currentVerse: String,
    onAsk: (String) -> Unit
) {
    val prompt = "Sugiere cómo continuar o mejorar este verso de un poema en español: \"$currentVerse\". " +
                 "Da solo 2-3 alternativas cortas, poéticas y en español."

    Column {
        Text(
            text = if (state.aiLevel == DeviceAILevel.LEVEL_GEMINI_NANO)
                "Gemini Nano puede sugerirte continuaciones para tu verso."
            else
                "El modelo LLM local puede sugerirte continuaciones.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Spacer(Modifier.height(Dimens.PaddingSmall))

        Button(
            onClick = { if (currentVerse.isNotBlank()) onAsk(prompt) },
            enabled = !state.isLoadingLlm && currentVerse.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoadingLlm) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                Spacer(Modifier.width(8.dp))
            }
            Text(
                if (state.isLoadingLlm) "Generando…" else "✨ Sugerir continuación"
            )
        }

        if (state.llmResponse.isNotBlank()) {
            Spacer(Modifier.height(Dimens.PaddingSmall))
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = state.llmResponse,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(Dimens.PaddingMedium)
                )
            }
        }

        state.llmError?.let { err ->
            Spacer(Modifier.height(4.dp))
            Text(
                text = err,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
