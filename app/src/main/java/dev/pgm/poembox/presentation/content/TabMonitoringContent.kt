package dev.pgm.poembox.presentation.content

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pgm.poembox.presentation.viewmodels.MonitoringViewModel

@Composable
fun MonitoringScreen(
    viewModel: MonitoringViewModel = hiltViewModel()
) {
    val state by viewModel.state
    val context = LocalContext.current

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
                    appendLine("--- Análisis métrico ---")
                    appendLine(state.syllablesAnalysis)
                }
                if (state.versesAnalysis.isNotBlank()) {
                    appendLine()
                    appendLine("--- Estructura ---")
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
                        "Guarda un borrador en el Editor",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "para ver el análisis aquí.",
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
                    IconButton(onClick = { viewModel.loadPoem() }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Actualizar análisis",
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
                        context.startActivity(Intent.createChooser(intent, "Compartir poema"))
                    }) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Compartir poema",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = {
                        val safeName = state.title.replace(Regex("[^a-zA-Z0-9áéíóúÁÉÍÓÚñÑ _-]"), "_")
                        exportLauncher.launch("$safeName.txt")
                    }) {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = "Exportar como .txt",
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
                title = "Análisis métrico",
                content = state.syllablesAnalysis,
                icon = Icons.Default.Straighten
            )

            AnalysisCard(
                title = "Estructura",
                content = state.versesAnalysis,
                icon = Icons.Default.Analytics
            )

            AnalysisCard(
                title = "Rima y estilo",
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
                    text = if (state.isValidated) "Poema validado ✓" else "Validar poema",
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
                        text = "Esperando análisis...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}
