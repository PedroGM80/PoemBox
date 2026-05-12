package dev.pgm.poembox.presentation.content

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

    LaunchedEffect(Unit) {
        viewModel.loadPoem()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (state.title.isBlank()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "No poem loaded. Save a draft in the Editor first.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            Text(
                text = state.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = state.body,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 24.sp
                )
            }

            Button(
                onClick = { viewModel.loadPoem() },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Refresh Analysis")
            }

            AnalysisCard(
                title = "Metrical Analysis",
                content = state.syllablesAnalysis,
                icon = Icons.Default.Straighten
            )

            AnalysisCard(
                title = "Structure",
                content = state.versesAnalysis,
                icon = Icons.Default.Analytics
            )

            AnalysisCard(
                title = "Rhyme & Style",
                content = "${state.rhymeAnalysis}\n\n${state.enjambmentAnalysis}",
                icon = Icons.Default.Brush
            )

            Button(
                onClick = { viewModel.validatePoem() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(56.dp),
                enabled = !state.isValidated,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state.isValidated) Color.Gray else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(if (state.isValidated) "Poem Validated" else "Validate Poem")
            }
        }
    }
}

@Composable
fun AnalysisCard(title: String, content: String, icon: ImageVector) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (content.isNotBlank()) {
                    Text(text = content, style = MaterialTheme.typography.bodyMedium)
                } else {
                    Text(text = "Waiting for analysis...", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
