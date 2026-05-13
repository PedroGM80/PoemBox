package dev.pgm.poembox.presentation.content

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pgm.poembox.R
import dev.pgm.poembox.domain.WritingPrompts
import dev.pgm.poembox.domain.model.LineValidation
import dev.pgm.poembox.domain.model.PoeticForms
import dev.pgm.poembox.presentation.theme.Shapes
import dev.pgm.poembox.presentation.theme.Typography
import dev.pgm.poembox.presentation.viewmodels.AuthViewModel
import dev.pgm.poembox.presentation.viewmodels.EditViewModel
import dev.pgm.poembox.presentation.viewmodels.GeminiViewModel

@Composable
private fun LineValidationRow(v: LineValidation) {
    val expected = v.expectedSyllables
    val syllableColor = when {
        expected == null -> MaterialTheme.colorScheme.primary
        v.syllableOk -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.error
    }
    val rhymeBadgeColor: Color = when (v.rhymesOk) {
        true -> MaterialTheme.colorScheme.tertiary
        false -> MaterialTheme.colorScheme.error
        null -> MaterialTheme.colorScheme.outline
    }
    val syllableLabel = when {
        expected == null -> stringResource(R.string.form_syllable_free, v.actualSyllables)
        v.syllableOk -> stringResource(R.string.form_syllable_ok, v.actualSyllables)
        else -> stringResource(R.string.form_syllable_error, v.actualSyllables, expected)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rhyme letter badge
        val rhymeLetter = v.rhymeLetter
        if (rhymeLetter != null) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = rhymeBadgeColor.copy(alpha = 0.15f),
                modifier = Modifier.padding(end = 6.dp)
            ) {
                Text(
                    text = rhymeLetter.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = rhymeBadgeColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                )
            }
        } else {
            Spacer(Modifier.width(24.dp))
        }
        Text(
            text = "${v.index + 1}. ${v.lineText.take(26)}${if (v.lineText.length > 26) "…" else ""}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        // Rhyme hint if needed
        val rhymeHint = v.rhymeHint
        if (rhymeHint != null && v.rhymesOk == false) {
            Text(
                text = rhymeHint,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                modifier = Modifier.padding(end = 6.dp)
            )
        }
        Text(
            text = syllableLabel,
            style = MaterialTheme.typography.labelSmall,
            color = syllableColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EditScreen(
    viewModel: EditViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    geminiViewModel: GeminiViewModel = hiltViewModel()
) {
    val title by viewModel.title
    val content by viewModel.content
    val analysisResult by viewModel.analysisResult
    val isSaved by viewModel.isSaved
    val wordCount by viewModel.wordCount
    val annotation by viewModel.annotation
    val lineValidations by viewModel.lineValidations
    val selectedForm by viewModel.selectedForm
    var showNotes by remember { mutableStateOf(false) }
    var showValidation by remember { mutableStateOf(false) }
    var showInspiration by remember { mutableStateOf(false) }
    val todayPrompt = remember { WritingPrompts.todayPrompt() }
    val userName by authViewModel.userName.collectAsState()
    val dailyReminderEnabled by viewModel.dailyReminderEnabled.collectAsState()
    val geminiState by geminiViewModel.state.collectAsState()
    val geminiApiKey by geminiViewModel.apiKey.collectAsState()
    var showApiKeyDialog by remember { mutableStateOf(false) }
    var apiKeyInput by remember { mutableStateOf("") }
    val context = LocalContext.current

    if (showApiKeyDialog) {
        AlertDialog(
            onDismissRequest = { showApiKeyDialog = false },
            title = { Text(stringResource(R.string.gemini_api_key_dialog_title)) },
            text = {
                Column {
                    Text(
                        stringResource(R.string.gemini_api_key_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    TextField(
                        value = apiKeyInput,
                        onValueChange = { apiKeyInput = it },
                        label = { Text(stringResource(R.string.gemini_api_key_label)) },
                        singleLine = true,
                        shape = Shapes.medium,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    geminiViewModel.saveApiKey(apiKeyInput)
                    showApiKeyDialog = false
                }) { Text(stringResource(R.string.gemini_api_key_save)) }
            },
            dismissButton = {
                TextButton(onClick = { showApiKeyDialog = false }) {
                    Text(stringResource(android.R.string.cancel))
                }
            }
        )
    }

    val unknownAuthor = stringResource(R.string.editor_unknown_author)

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = title,
                    onValueChange = { viewModel.onTitleChange(it) },
                    label = { Text(text = stringResource(R.string.editor_title_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    textStyle = TextStyle(
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    supportingText = {
                        Text(
                            text = "${title.length}/60",
                            color = if (title.length >= 55)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.outline
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    shape = Shapes.medium,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
                IconButton(onClick = { viewModel.clearPoem() }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.editor_new_poem_description),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Form selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PoeticForms.ALL.forEach { form ->
                    FilterChip(
                        selected = selectedForm.id == form.id,
                        onClick = { viewModel.onFormSelected(form) },
                        label = {
                            Text(
                                stringResource(form.nameRes),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                }
            }

            // Progress indicator for fixed-length forms
            if (selectedForm.totalLines > 0) {
                val current = lineValidations.size
                val total = selectedForm.totalLines
                val complete = current >= total
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    LinearProgressIndicator(
                        progress = { (current.toFloat() / total).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp),
                        color = if (complete) MaterialTheme.colorScheme.tertiary
                                else MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (complete) stringResource(R.string.form_complete)
                               else stringResource(R.string.form_progress, current, total),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (complete) MaterialTheme.colorScheme.tertiary
                                else MaterialTheme.colorScheme.outline
                    )
                }
            }

            // Daily inspiration card
            TextButton(
                onClick = { showInspiration = !showInspiration },
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(
                    imageVector = if (showInspiration) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(stringResource(R.string.editor_inspiration_title), style = MaterialTheme.typography.labelMedium)
            }
            AnimatedVisibility(visible = showInspiration) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "\"$todayPrompt\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        TextButton(
                            onClick = { viewModel.onTitleChange(todayPrompt) }
                        ) {
                            Text(
                                stringResource(R.string.editor_inspiration_use_title),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                TextField(
                    value = content,
                    onValueChange = { viewModel.onContentChange(it) },
                    placeholder = { Text(text = stringResource(R.string.editor_content_placeholder)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    textStyle = TextStyle(
                        fontSize = Typography.bodyLarge.fontSize,
                        lineHeight = 28.sp
                    ),
                    modifier = Modifier.fillMaxSize(),
                    shape = Shapes.medium,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    )
                )

                if (analysisResult.isNotBlank()) {
                    Text(
                        text = analysisResult,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            if (wordCount > 0) {
                Text(
                    text = stringResource(R.string.editor_word_count, wordCount),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(end = 4.dp, top = 2.dp)
                )
            }

            if (lineValidations.isNotEmpty()) {
                TextButton(
                    onClick = { showValidation = !showValidation },
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Icon(
                        imageVector = if (showValidation) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.editor_syllables_per_verse), style = MaterialTheme.typography.labelMedium)
                }
                AnimatedVisibility(visible = showValidation) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            lineValidations.forEach { v ->
                                LineValidationRow(v)
                            }
                        }
                    }
                }
            }

            TextButton(
                onClick = { showNotes = !showNotes },
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(
                    imageVector = if (showNotes) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(stringResource(R.string.editor_notes_section), style = MaterialTheme.typography.labelMedium)
            }
            AnimatedVisibility(visible = showNotes) {
                TextField(
                    value = annotation,
                    onValueChange = { viewModel.onAnnotationChange(it) },
                    placeholder = { Text(stringResource(R.string.editor_notes_placeholder)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = Shapes.medium,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ),
                    maxLines = 4
                )
            }

            // Gemini AI verse suggestion
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = {
                        if (geminiApiKey.isBlank()) {
                            showApiKeyDialog = true
                        } else {
                            geminiViewModel.suggestNextVerse(content, selectedForm, geminiApiKey)
                        }
                    },
                    enabled = content.isNotBlank() && !geminiState.isLoading
                ) {
                    if (geminiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.gemini_generating), style = MaterialTheme.typography.labelMedium)
                    } else {
                        Text(stringResource(R.string.gemini_continue_verse), style = MaterialTheme.typography.labelMedium)
                    }
                }
                if (geminiApiKey.isNotBlank()) {
                    TextButton(onClick = { showApiKeyDialog = true }) {
                        Text("⚙", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }

            AnimatedVisibility(visible = geminiState.suggestion.isNotBlank() || geminiState.error.isNotBlank()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (geminiState.error.isNotBlank())
                            MaterialTheme.colorScheme.errorContainer
                        else
                            MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        if (geminiState.error.isNotBlank()) {
                            Text(
                                geminiState.error,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        } else {
                            Text(
                                stringResource(R.string.gemini_suggestion_label),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                            )
                            Text(
                                "\"${geminiState.suggestion}\"",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                TextButton(onClick = { geminiViewModel.clearSuggestion() }) {
                                    Text(stringResource(android.R.string.cancel), style = MaterialTheme.typography.labelSmall)
                                }
                                TextButton(onClick = {
                                    val newContent = if (content.endsWith("\n")) content + geminiState.suggestion
                                                     else "$content\n${geminiState.suggestion}"
                                    viewModel.onContentChange(newContent)
                                    geminiViewModel.clearSuggestion()
                                }) {
                                    Text(stringResource(R.string.gemini_use_suggestion), style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.settings_daily_reminder),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Switch(
                    checked = dailyReminderEnabled,
                    onCheckedChange = { viewModel.setDailyReminder(it) }
                )
            }

            Button(
                onClick = {
                    viewModel.saveDraft(userName ?: unknownAuthor) {
                        Toast.makeText(context, context.getString(R.string.editor_saved_toast), Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = title.isNotBlank() && content.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSaved) MaterialTheme.colorScheme.secondary
                    else MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(56.dp)
            ) {
                Text(
                    text = stringResource(if (isSaved) R.string.editor_saved_button else R.string.editor_save_button),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
