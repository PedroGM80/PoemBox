package dev.pgm.poembox.presentation.content

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush as GradientBrush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pgm.poembox.R
import dev.pgm.poembox.presentation.theme.Dimens
import dev.pgm.poembox.presentation.theme.ImmersiveDarkBackground
import dev.pgm.poembox.presentation.theme.ImmersiveDarkText
import dev.pgm.poembox.presentation.theme.ImmersiveWarmBackground
import dev.pgm.poembox.presentation.theme.ImmersiveWarmText
import dev.pgm.poembox.presentation.theme.PoeticFont
import dev.pgm.poembox.presentation.util.Analytics
import dev.pgm.poembox.presentation.util.InAppReviewHelper
import dev.pgm.poembox.presentation.util.PdfExporter
import dev.pgm.poembox.presentation.util.PoemCardRenderer
import dev.pgm.poembox.presentation.viewmodels.MonitoringViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// ─────────────────────────────────────────────────────────────────────────────
// Immersive reading dialog
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ImmersiveReadingDialog(title: String, body: String, onDismiss: () -> Unit) {
    var warmBackground by remember { mutableStateOf(false) }
    val bgColor = if (warmBackground) ImmersiveWarmBackground else ImmersiveDarkBackground
    val textColor = if (warmBackground) ImmersiveWarmText else ImmersiveDarkText

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Dimens.PaddingExtraLarge)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(72.dp))
                Text(
                    text = title,
                    fontFamily = PoeticFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = textColor,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(Dimens.PaddingExtraLarge))
                Text(
                    text = body,
                    fontFamily = PoeticFont,
                    fontSize = 22.sp,
                    lineHeight = 38.sp,
                    color = textColor,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(80.dp))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(Dimens.PaddingSmall),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = stringResource(R.string.monitor_immersive_close), tint = textColor)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (warmBackground) stringResource(R.string.monitor_immersive_bg_warm)
                               else stringResource(R.string.monitor_immersive_bg_dark),
                        style = MaterialTheme.typography.labelMedium,
                        color = textColor
                    )
                    Switch(
                        checked = warmBackground,
                        onCheckedChange = { warmBackground = it },
                        modifier = Modifier.padding(start = Dimens.PaddingMedium, end = Dimens.PaddingSmall)
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Poem body card — shows the poem over an optional background image
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PoemBodyCard(
    body: String,
    bgImageUri: Uri?,
    onPickImage: () -> Unit,
    onClearImage: () -> Unit
) {
    val context = LocalContext.current
    val shape = MaterialTheme.shapes.large

    val loadedBitmap by produceState<android.graphics.Bitmap?>(
        initialValue = null,
        key1 = bgImageUri
    ) {
        val bmp = if (bgImageUri != null) {
            withContext(Dispatchers.IO) {
                try {
                    context.contentResolver.openInputStream(bgImageUri)
                        ?.use { BitmapFactory.decodeStream(it) }
                } catch (e: Exception) { null }
            }
        } else null
        value = bmp
        awaitDispose { bmp?.recycle() }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(shape)
            .then(
                if (loadedBitmap != null) {
                    Modifier.paint(
                        painter = BitmapPainter(loadedBitmap!!.asImageBitmap()),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                }
            )
    ) {
        // Gradient overlay — stronger when a photo is present
        val overlayAlpha = if (loadedBitmap != null) 0.60f else 0f
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    GradientBrush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = overlayAlpha * 0.5f),
                            Color.Black.copy(alpha = overlayAlpha),
                            Color.Black.copy(alpha = overlayAlpha * 0.8f)
                        )
                    )
                )
        )

        val textColor = if (loadedBitmap != null) Color.White
                        else MaterialTheme.colorScheme.onSurfaceVariant

        // Poem text — scrollable within the fixed-height card
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.PaddingLarge)
                .padding(top = Dimens.PaddingLarge, bottom = 56.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalDivider(
                modifier = Modifier.width(Dimens.DividerWidthSmall),
                color = textColor.copy(alpha = 0.45f),
                thickness = 1.dp
            )
            Spacer(Modifier.height(Dimens.PaddingMedium))
            Text(
                text = body,
                fontFamily = PoeticFont,
                fontSize = 19.sp,
                lineHeight = 32.sp,
                color = textColor,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(Dimens.PaddingMedium))
            HorizontalDivider(
                modifier = Modifier.width(Dimens.DividerWidthSmall),
                color = textColor.copy(alpha = 0.45f),
                thickness = 1.dp
            )
        }

        // Bottom-right controls: clear + pick-image button
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(Dimens.PaddingSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (bgImageUri != null) {
                TextButton(
                    onClick = onClearImage,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.White.copy(alpha = 0.85f)
                    )
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        stringResource(R.string.monitor_body_card_bg_clear),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            FilledIconButton(
                onClick = onPickImage,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (bgImageUri != null)
                        MaterialTheme.colorScheme.secondary
                    else
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
                    contentColor = if (bgImageUri != null)
                        MaterialTheme.colorScheme.onSecondary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Default.Wallpaper,
                    contentDescription = stringResource(R.string.monitor_bg_image_description),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Share section — prominent row of labeled share actions
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ShareRow(
    onShareText: () -> Unit,
    onShareCard: () -> Unit,
    onSharePdf: () -> Unit,
    onShareTxt: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.SpacingSmall),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(Dimens.PaddingLarge)) {
            Text(
                text = stringResource(R.string.monitor_share_section_title),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(Dimens.PaddingMedium))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ShareAction(
                    icon = Icons.Default.Share,
                    label = stringResource(R.string.monitor_share_action_text),
                    onClick = onShareText
                )
                ShareAction(
                    icon = Icons.Default.Image,
                    label = stringResource(R.string.monitor_share_action_card),
                    onClick = onShareCard
                )
                ShareAction(
                    icon = Icons.Default.PictureAsPdf,
                    label = stringResource(R.string.monitor_share_action_pdf),
                    onClick = onSharePdf
                )
                ShareAction(
                    icon = Icons.Default.Download,
                    label = stringResource(R.string.monitor_share_action_txt),
                    onClick = onShareTxt
                )
            }
        }
    }
}

@Composable
private fun ShareAction(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(horizontal = Dimens.PaddingMedium, vertical = Dimens.PaddingSmall)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(Dimens.IconLarge)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            textAlign = TextAlign.Center
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Main monitoring screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun MonitoringScreen(
    viewModel: MonitoringViewModel = hiltViewModel()
) {
    val state by viewModel.state
    val context = LocalContext.current
    val metricalSectionText = stringResource(R.string.monitor_export_section_metrical)
    val structureSectionText = stringResource(R.string.monitor_export_section_structure)
    val shareChooserText = stringResource(R.string.monitor_share_chooser)
    var showImmersive by remember { mutableStateOf(false) }
    var bgImageUri by remember { mutableStateOf<Uri?>(null) }

    // Solicitar valoración + analytics cuando el usuario valida un poema
    LaunchedEffect(state.isValidated) {
        if (state.isValidated) {
            Analytics.poemValidated(state.title, syllables = 0)
            InAppReviewHelper.requestReview(context)
        }
    }

    val bgImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> bgImageUri = uri }

    if (showImmersive) {
        ImmersiveReadingDialog(
            title = state.title,
            body = state.body,
            onDismiss = { showImmersive = false }
        )
    }

    val exportPdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        uri?.let {
            PdfExporter.exportToPdf(
                context, it,
                state.title, state.body,
                state.syllablesAnalysis, state.versesAnalysis,
                state.rhymeAnalysis, state.enjambmentAnalysis
            )
        }
    }

    val exportTxtLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        uri?.let {
            val exportContent = buildString {
                appendLine(state.title)
                appendLine()
                appendLine(state.body)
                if (state.syllablesAnalysis.isNotBlank()) {
                    appendLine()
                    appendLine(metricalSectionText)
                    appendLine(state.syllablesAnalysis)
                }
                if (state.versesAnalysis.isNotBlank()) {
                    appendLine()
                    appendLine(structureSectionText)
                    appendLine(state.versesAnalysis)
                }
            }
            context.contentResolver.openOutputStream(it)?.use { stream ->
                stream.write(exportContent.toByteArray())
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.PaddingLarge, vertical = Dimens.PaddingMedium)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.title.isBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 80.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                    Spacer(Modifier.height(Dimens.PaddingLarge))
                    Text(
                        stringResource(R.string.monitor_no_poem_title),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        stringResource(R.string.monitor_no_poem_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            // Title row: poem title + immersive reading + refresh
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { Analytics.immersiveOpened(); showImmersive = true }) {
                    Icon(
                        Icons.Default.MenuBook,
                        contentDescription = stringResource(R.string.monitor_read_description),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = { viewModel.loadPoem() }) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.monitor_refresh_description),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(Dimens.PaddingMedium))

            // Poem body card with optional background image
            PoemBodyCard(
                body = state.body,
                bgImageUri = bgImageUri,
                onPickImage = { Analytics.bgImagePicked(); bgImagePickerLauncher.launch("image/*") },
                onClearImage = { bgImageUri = null }
            )

            Spacer(Modifier.height(Dimens.PaddingMedium))

            // Prominent share section
            ShareRow(
                onShareText = {
                    Analytics.sharedAsText()
                    val shareText = buildString {
                        appendLine(state.title)
                        appendLine()
                        append(state.body)
                    }
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        putExtra(Intent.EXTRA_SUBJECT, state.title)
                    }
                    context.startActivity(Intent.createChooser(intent, shareChooserText))
                },
                onShareCard = {
                    Analytics.sharedAsCard(hasBgImage = bgImageUri != null)
                    val uri = PoemCardRenderer.createAndShare(
                        context, state.title, state.body,
                        darkMode = true,
                        backgroundImageUri = bgImageUri
                    )
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "image/png"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(intent, shareChooserText))
                },
                onSharePdf = {
                    Analytics.sharedAsPdf()
                    val safeName = state.title.replace(Regex("[^a-zA-Z0-9áéíóúÁÉÍÓÚñÑ _-]"), "_")
                    exportPdfLauncher.launch("$safeName.pdf")
                },
                onShareTxt = {
                    Analytics.sharedAsTxt()
                    val safeName = state.title.replace(Regex("[^a-zA-Z0-9áéíóúÁÉÍÓÚñÑ _-]"), "_")
                    exportTxtLauncher.launch("$safeName.txt")
                }
            )

            Spacer(Modifier.height(Dimens.PaddingMedium))

            // Analysis cards
            AnalysisCard(
                title = stringResource(R.string.monitor_card_metrical),
                content = state.syllablesAnalysis,
                icon = Icons.Default.Straighten
            )

            AnalysisCard(
                title = stringResource(R.string.monitor_card_structure),
                content = state.versesAnalysis,
                icon = Icons.Default.Analytics
            )

            AnalysisCard(
                title = stringResource(R.string.monitor_card_rhyme),
                content = buildString {
                    if (state.rhymeAnalysis.isNotBlank()) appendLine(state.rhymeAnalysis)
                    if (state.enjambmentAnalysis.isNotBlank()) append(state.enjambmentAnalysis)
                },
                icon = Icons.Default.Brush
            )

            Spacer(Modifier.height(Dimens.PaddingMedium))

            // Validate button
            Button(
                onClick = { viewModel.validatePoem() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.ButtonHeight),
                enabled = !state.isValidated,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state.isValidated)
                        MaterialTheme.colorScheme.secondary
                    else
                        MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(
                    text = stringResource(if (state.isValidated) R.string.monitor_validated_button else R.string.monitor_validate_button),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.height(Dimens.PaddingLarge))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Analysis card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AnalysisCard(title: String, content: String, icon: ImageVector) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.SpacingSmall),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(Dimens.PaddingLarge)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(Dimens.RhymeAnnotationWidth)
                    .padding(top = Dimens.SpacingTiny)
            )
            Spacer(Modifier.width(Dimens.PaddingLarge))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(Dimens.PaddingSmall))
                if (content.isNotBlank()) {
                    Text(text = content, style = MaterialTheme.typography.bodyMedium)
                } else {
                    Text(
                        text = stringResource(R.string.monitor_waiting),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}
