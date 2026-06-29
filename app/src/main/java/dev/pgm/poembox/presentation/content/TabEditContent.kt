package dev.pgm.poembox.presentation.content

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pgm.poembox.R
import dev.pgm.poembox.domain.Constants
import dev.pgm.poembox.domain.WritingPrompts
import dev.pgm.poembox.domain.model.LineValidation
import dev.pgm.poembox.domain.model.PoeticForms
import dev.pgm.poembox.domain.model.PoeticFormDef
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

    var currentStep by remember { mutableStateOf(1) }
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

    val userName by authViewModel.userName.collectAsState()
    val dailyReminderEnabled by viewModel.dailyReminderEnabled.collectAsState()
    val context = LocalContext.current
    val unknownAuthor = stringResource(R.string.editor_unknown_author)
    val savedToastText = stringResource(R.string.editor_saved_toast)

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = Dimens.PaddingNormal,
                    vertical = Dimens.PaddingLarge
                )
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Step indicator - improved visibility
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimens.PaddingXLarge),
                horizontalArrangement = Arrangement.spacedBy(Dimens.PagerIndicatorSpacing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { step ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(Dimens.ProgressBarHeight)
                            .background(
                                color = if (step + 1 <= currentStep)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(Dimens.CardCornerRadius)
                            )
                    )
                }
            }

            // Step number indicator for clarity
            Text(
                text = stringResource(R.string.editor_step_number, currentStep, 3),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = Dimens.PaddingSmall)
            )

            // Step content
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.TopCenter
            ) {
                when (currentStep) {
                    1 -> StepTitle(title, viewModel, selectedForm, { showFormsLibrary = true })
                    2 -> StepContent(content, viewModel, wordCount)
                    3 -> StepAnnotations(annotation, viewModel, dailyReminderEnabled)
                }
            }

            // Navigation buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.PaddingLarge),
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium)
            ) {
                if (currentStep > 1) {
                    OutlinedButton(
                        onClick = { currentStep-- },
                        modifier = Modifier
                            .weight(1f)
                            .height(Dimens.ButtonHeight)
                    ) {
                        Text(
                            stringResource(R.string.editor_previous),
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }

                if (currentStep < 3) {
                    Button(
                        onClick = { currentStep++ },
                        enabled = when (currentStep) {
                            1 -> title.isNotBlank()
                            2 -> content.isNotBlank()
                            else -> true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(Dimens.ButtonHeight)
                    ) {
                        Text(
                            stringResource(R.string.editor_next),
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            viewModel.saveDraft(userName ?: unknownAuthor) {
                                Toast.makeText(context, savedToastText, Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = title.isNotBlank() && content.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSaved) MaterialTheme.colorScheme.secondary
                            else MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .weight(1f)
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
    }
}

@Composable
private fun StepTitle(
    title: String,
    viewModel: EditViewModel,
    selectedForm: PoeticFormDef,
    onShowFormsLibrary: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.editor_step_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = Dimens.PaddingLarge)
        )

        TextField(
            value = title,
            onValueChange = { viewModel.onTitleChange(it) },
            label = { Text(stringResource(R.string.editor_title_label)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            textStyle = MaterialTheme.typography.headlineMedium.copy(
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            ),
            supportingText = {
                Text(
                    text = "${title.length}/${Constants.MAX_TITLE_LENGTH}",
                    color = if (title.length >= Constants.TITLE_LENGTH_WARNING_THRESHOLD)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = Dimens.PaddingNormal,
                    vertical = Dimens.PaddingLarge
                ),
            shape = Shapes.medium,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary
            ),
            singleLine = true
        )

        Text(
            text = stringResource(R.string.editor_form_label),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(
                horizontal = Dimens.PaddingNormal,
                vertical = Dimens.PaddingMedium
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(
                    horizontal = Dimens.PaddingNormal,
                    vertical = Dimens.PaddingMedium
                ),
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingLarge)
        ) {
            PoeticForms.ALL.forEach { form ->
                FilterChip(
                    selected = selectedForm.id == form.id,
                    onClick = { viewModel.onFormSelected(form) },
                    label = {
                        Text(
                            stringResource(form.nameRes),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                )
            }
        }

        FilledTonalButton(
            onClick = onShowFormsLibrary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = Dimens.PaddingNormal,
                    vertical = Dimens.PaddingMedium
                )
                .height(Dimens.ButtonHeight)
        ) {
            Text(
                stringResource(R.string.editor_see_examples),
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

@Composable
private fun StepContent(
    content: String,
    viewModel: EditViewModel,
    wordCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Text(
            text = stringResource(R.string.editor_step_poem),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(
                horizontal = Dimens.PaddingNormal,
                vertical = Dimens.PaddingLarge
            )
        )

        TextField(
            value = content,
            onValueChange = { viewModel.onContentChange(it) },
            placeholder = { Text(stringResource(R.string.editor_content_placeholder)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Default
            ),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = Dimens.PaddingNormal),
            shape = Shapes.medium,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary
            )
        )

        Text(
            text = stringResource(R.string.editor_word_count, wordCount),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(
                horizontal = Dimens.PaddingNormal,
                vertical = Dimens.PaddingMedium
            )
        )
    }
}

@Composable
private fun StepAnnotations(
    annotation: String,
    viewModel: EditViewModel,
    dailyReminderEnabled: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Text(
            text = stringResource(R.string.editor_step_notes),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(
                horizontal = Dimens.PaddingNormal,
                vertical = Dimens.PaddingLarge
            )
        )

        TextField(
            value = annotation,
            onValueChange = { viewModel.onAnnotationChange(it) },
            placeholder = { Text(stringResource(R.string.editor_notes_placeholder)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Default
            ),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = Dimens.PaddingNormal),
            shape = Shapes.medium,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = Dimens.PaddingNormal,
                    vertical = Dimens.PaddingLarge
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.settings_daily_reminder),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Switch(
                checked = dailyReminderEnabled,
                onCheckedChange = { viewModel.setDailyReminder(it) }
            )
        }
    }
}
