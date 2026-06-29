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
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.text.input.KeyboardCapitalization
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
import dev.pgm.poembox.presentation.screens.FormsLibraryDialog
import dev.pgm.poembox.presentation.viewmodels.EditViewModel

@Composable
fun EditScreen(
    viewModel: EditViewModel = hiltViewModel()
) {
    val title by viewModel.title
    val content by viewModel.content
    val isSaved by viewModel.isSaved
    val wordCount by viewModel.wordCount
    val annotation by viewModel.annotation
    val author by viewModel.author
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

    val dailyReminderEnabled by viewModel.dailyReminderEnabled.collectAsState()
    val context = LocalContext.current
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
                    3 -> StepAnnotations(annotation, author, viewModel, dailyReminderEnabled)
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
                            viewModel.saveDraft {
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
                capitalization = KeyboardCapitalization.Sentences,
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
            .verticalScroll(rememberScrollState())
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
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Default
            ),
            // Un poema tiene varios versos: campo multilínea con altura mínima
            // fija (no weight) para que el teclado no lo aplaste ni recorte el texto.
            singleLine = false,
            minLines = 6,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 220.dp)
                .padding(horizontal = Dimens.PaddingNormal),
            shape = Shapes.medium,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
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
    author: String,
    viewModel: EditViewModel,
    dailyReminderEnabled: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
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

        // Campo de autor (opcional)
        OutlinedTextField(
            value = author,
            onValueChange = { viewModel.onAuthorChange(it) },
            label = { Text(stringResource(R.string.editor_author_label)) },
            placeholder = { Text(stringResource(R.string.editor_author_placeholder)) },
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.PaddingNormal),
            shape = Shapes.medium
        )

        Spacer(Modifier.height(Dimens.PaddingLarge))

        // Notas libres (opcional)
        Text(
            text = stringResource(R.string.editor_notes_section),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(
                horizontal = Dimens.PaddingNormal,
                vertical = Dimens.PaddingSmall
            )
        )

        TextField(
            value = annotation,
            onValueChange = { viewModel.onAnnotationChange(it) },
            placeholder = { Text(stringResource(R.string.editor_notes_placeholder)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Default
            ),
            singleLine = false,
            minLines = 3,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp)
                .padding(horizontal = Dimens.PaddingNormal),
            shape = Shapes.medium,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
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
