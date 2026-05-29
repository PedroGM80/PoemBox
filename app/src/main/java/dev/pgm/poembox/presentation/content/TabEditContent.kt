package dev.pgm.poembox.presentation.content

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pgm.poembox.R
import dev.pgm.poembox.domain.Constants
import dev.pgm.poembox.domain.WritingPrompts
import dev.pgm.poembox.domain.model.LineValidation
import dev.pgm.poembox.domain.model.PoeticForms
import dev.pgm.poembox.presentation.theme.Dimens
import dev.pgm.poembox.presentation.theme.Shapes
import dev.pgm.poembox.presentation.theme.Typography
import dev.pgm.poembox.presentation.ai.PoetryAssistantPanel
import dev.pgm.poembox.presentation.screens.FormsLibraryDialog
import dev.pgm.poembox.presentation.viewmodels.AuthViewModel
import dev.pgm.poembox.presentation.viewmodels.EditViewModel

@Composable
private fun LineValidationRow(v: LineValidation) {
    val expected = v.expectedSyllables
    val syllableColor = when {
        expected == null -> MaterialTheme.colorScheme.primary
        v.syllableOk -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.error
    }
    val rhymeBadgeColor: Color = when (v.rhymesOk) {
        true -> MaterialTheme.colorScheme.secondary
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
            .padding(vertical = Dimens.SpacingSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rhyme letter annotation
        val rhymeLetter = v.rhymeLetter
        if (rhymeLetter != null) {
            Text(
                text = rhymeLetter.uppercase(),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                ),
                color = rhymeBadgeColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(Dimens.RhymeAnnotationWidth),
                textAlign = TextAlign.Center
            )
        } else {
            Spacer(Modifier.width(Dimens.RhymeAnnotationWidth))
        }
        Text(
            text = "${v.index + 1}. ${v.lineText}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            modifier = Modifier.weight(1f),
            maxLines = 1
        )
        
        Text(
            text = syllableLabel,
            style = MaterialTheme.typography.labelSmall,
            color = syllableColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun EditScreen(
    viewModel: EditViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
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
    var showFormsLibrary by remember { mutableStateOf(false) }

    if (showFormsLibrary) {
        FormsLibraryDialog(
            onDismiss = { showFormsLibrary = false },
            onUseExample = { exampleTitle, examplePoem ->
                viewModel.onTitleChange(exampleTitle)
                viewModel.onContentChange(examplePoem)
            }
        )
    }
    val todayPrompt = remember { WritingPrompts.todayPrompt() }
    val userName by authViewModel.userName.collectAsState()
    val dailyReminderEnabled by viewModel.dailyReminderEnabled.collectAsState()
    val context = LocalContext.current

    val unknownAuthor = stringResource(R.string.editor_unknown_author)

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.PaddingLarge)
                .imePadding()
                .animateContentSize(),
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
                    textStyle = MaterialTheme.typography.headlineSmall.copy(
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    ),
                    supportingText = {
                        Text(
                            text = "${title.length}/${Constants.MAX_TITLE_LENGTH}",
                            color = if (title.length >= Constants.TITLE_LENGTH_WARNING_THRESHOLD)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.outline,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = Dimens.PaddingMedium),
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
                    .padding(vertical = Dimens.SpacingSmall),
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium)
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
                        .padding(bottom = Dimens.SpacingSmall),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    LinearProgressIndicator(
                        progress = { (current.toFloat() / total).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .weight(1f)
                            .height(Dimens.ProgressBarHeight),
                        color = if (complete) MaterialTheme.colorScheme.secondary
                                else MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(Dimens.SpacingMedium))
                    Text(
                        text = if (complete) stringResource(R.string.form_complete)
                               else stringResource(R.string.form_progress, current, total),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (complete) MaterialTheme.colorScheme.secondary
                                else MaterialTheme.colorScheme.outline
                    )
                }
            }

            // Botón biblioteca de formas + inspiración en la misma fila
            Row(modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = { showFormsLibrary = true }) {
                    Text(stringResource(R.string.forms_library_button), style = MaterialTheme.typography.labelMedium)
                }
                Spacer(Modifier.weight(1f))
            }

            // Daily inspiration card
            TextButton(
                onClick = { showInspiration = !showInspiration },
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(
                    imageVector = if (showInspiration) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(Dimens.IconSmall)
                )
                Spacer(Modifier.width(Dimens.SpacingSmall))
                Text(stringResource(R.string.editor_inspiration_title), style = MaterialTheme.typography.labelMedium)
            }
            AnimatedVisibility(visible = showInspiration) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Dimens.PaddingNormal),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    ),
                    shape = Shapes.medium,
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(Dimens.PaddingLarge)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.editor_inspiration_title),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(bottom = Dimens.PaddingSmall)
                        )
                        Text(
                            text = "\"$todayPrompt\"",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            ),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                            TextButton(
                                onClick = { viewModel.onTitleChange(todayPrompt) }
                            ) {
                                Text(
                                    stringResource(R.string.editor_inspiration_use_title),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = Dimens.SpacingMedium)
            ) {
                TextField(
                    value = content,
                    onValueChange = { viewModel.onContentChange(it) },
                    placeholder = { Text(text = stringResource(R.string.editor_content_placeholder)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    textStyle = MaterialTheme.typography.bodyLarge,
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
                            .padding(Dimens.PaddingLarge)
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer,
                                MaterialTheme.shapes.small
                            )
                            .padding(horizontal = Dimens.PaddingNormal, vertical = Dimens.PaddingSmall),
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
                        .padding(end = Dimens.PaddingSmall, top = Dimens.SpacingTiny)
                )
            }

            // Asistente poético IA — el verso activo es la última línea no vacía del contenido
            val activeVerse = remember(content) {
                content.trimEnd().lines().lastOrNull { it.isNotBlank() } ?: ""
            }
            PoetryAssistantPanel(
                currentVerse = activeVerse,
                onInsertWord = { word ->
                    val cursor = if (content.endsWith(" ") || content.isEmpty()) word else " $word"
                    viewModel.onContentChange(content + cursor)
                }
            )

            if (lineValidations.isNotEmpty()) {
                TextButton(
                    onClick = { showValidation = !showValidation },
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Icon(
                        imageVector = if (showValidation) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(Dimens.IconSmall)
                    )
                    Spacer(Modifier.width(Dimens.SpacingSmall))
                    Text(stringResource(R.string.editor_syllables_per_verse), style = MaterialTheme.typography.labelMedium)
                }
                AnimatedVisibility(visible = showValidation) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = Dimens.PaddingSmall),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = Dimens.PaddingNormal, vertical = Dimens.PaddingMedium)) {
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
                    modifier = Modifier.size(Dimens.IconSmall)
                )
                Spacer(Modifier.width(Dimens.SpacingSmall))
                Text(stringResource(R.string.editor_notes_section), style = MaterialTheme.typography.labelMedium)
            }
            AnimatedVisibility(visible = showNotes) {
                TextField(
                    value = annotation,
                    onValueChange = { viewModel.onAnnotationChange(it) },
                    placeholder = { Text(stringResource(R.string.editor_notes_placeholder)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Dimens.PaddingMedium),
                    shape = Shapes.medium,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ),
                    maxLines = 4
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.SpacingSmall),
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
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.PaddingLarge)
                    .height(Dimens.ButtonHeight)
            ) {
                Text(
                    text = stringResource(if (isSaved) R.string.editor_saved_button else R.string.editor_save_button),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
