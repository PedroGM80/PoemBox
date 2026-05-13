package dev.pgm.poembox.presentation.content

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import dev.pgm.poembox.domain.PdfExporter
import dev.pgm.poembox.domain.PoemCardRenderer
import dev.pgm.poembox.presentation.theme.PoeticFont
import dev.pgm.poembox.presentation.viewmodels.MonitoringViewModel

@Composable
private fun ImmersiveReadingDialog(title: String, body: String, onDismiss: () -> Unit) {
    var warmBackground by remember { mutableStateOf(false) }
    val bgColor = if (warmBackground) Color(0xFFFAF3E0) else Color(0xFF1A1A2E)
    val textColor = if (warmBackground) Color(0xFF3E2723) else Color(0xFFF5F0E8)

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
                    .padding(horizontal = 32.dp)
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
                Spacer(Modifier.height(32.dp))
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
                    .padding(4.dp),
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
                        modifier = Modifier.padding(start = 8.dp, end = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MonitoringScreen(
    viewModel: MonitoringViewModel = hiltViewModel()
) {
    val state by viewModel.state
    val context = LocalContext.current
    var showImmersive by remember { mutableStateOf(false) }

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

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        uri?.let {
            val exportContent = buildString {
                appendLine(state.title)
                appendLine()
                appendLine(state.body)
                if (state.syllablesAnalysis.isNotBlank()) {
                    appendLine()
                    appendLine(context.getString(R.string.monitor_export_section_metrical))
                    appendLine(state.syllablesAnalysis)
                }
                if (state.versesAnalysis.isNotBlank()) {
                    appendLine()
                    appendLine(context.getString(R.string.monitor_export_section_structure))
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
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                    Text("📝", style = MaterialTheme.typography.displayMedium)
                    Spacer(Modifier.height(16.dp))
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
                Row {
                    IconButton(onClick = { showImmersive = true }) {
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
                    IconButton(onClick = {
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
                        context.startActivity(Intent.createChooser(intent, context.getString(R.string.monitor_share_chooser)))
                    }) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = stringResource(R.string.monitor_share_description),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = {
                        val safeName = state.title.replace(Regex("[^a-zA-Z0-9áéíóúÁÉÍÓÚñÑ _-]"), "_")
                        exportLauncher.launch("$safeName.txt")
                    }) {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = stringResource(R.string.monitor_export_description),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = {
                        val safeName = state.title.replace(Regex("[^a-zA-Z0-9áéíóúÁÉÍÓÚñÑ _-]"), "_")
                        exportPdfLauncher.launch("$safeName.pdf")
                    }) {
                        Icon(
                            Icons.Default.PictureAsPdf,
                            contentDescription = stringResource(R.string.monitor_export_pdf_description),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = {
                        val uri = PoemCardRenderer.createAndShare(context, state.title, state.body, darkMode = true)
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "image/png"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(intent, context.getString(R.string.monitor_share_chooser)))
                    }) {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = stringResource(R.string.monitor_share_image_description),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 260.dp)
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = state.body,
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 28.sp
                )
            }

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

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { viewModel.validatePoem() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
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
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun AnalysisCard(title: String, content: String, icon: ImageVector) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(28.dp)
                    .padding(top = 2.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
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
