package dev.pgm.poembox.presentation.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pgm.poembox.presentation.components.TabItem
import dev.pgm.poembox.presentation.viewmodels.ManagerViewModel

data class PoemDetails(
    val title: String,
    val author: String,
    val date: String,
    val annotations: String,
    val poem: String
)

@Composable
fun PoemCard(
    poem: PoemDetails,
    onDelete: () -> Unit,
    onViewPoem: () -> Unit,
    onViewNotes: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = poem.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "By ${poem.author}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = poem.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Row {
                IconButton(onClick = onViewPoem) {
                    Icon(Icons.Default.Description, contentDescription = "View Poem")
                }
                IconButton(onClick = onViewNotes) {
                    Icon(Icons.Default.Notes, contentDescription = "View Notes")
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun ManagerScreen(
    viewModel: ManagerViewModel = hiltViewModel()
) {
    val poems by viewModel.poems
    val isLoading by viewModel.isLoading
    val showDialog = remember { mutableStateOf(false) }
    val dialogTitle = remember { mutableStateOf("") }
    val dialogBody = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadPoems()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.loadPoems() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (poems.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No validated poems yet.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(poems) { poem ->
                        PoemCard(
                            poem = poem,
                            onDelete = { viewModel.deletePoem(poem) },
                            onViewPoem = {
                                dialogTitle.value = poem.title
                                dialogBody.value = poem.poem
                                showDialog.value = true
                            },
                            onViewNotes = {
                                dialogTitle.value = "Notes: ${poem.title}"
                                dialogBody.value = poem.annotations.ifBlank { "No notes available." }
                                showDialog.value = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(dialogTitle.value) },
            text = { Text(dialogBody.value) },
            confirmButton = {
                TextButton(onClick = { showDialog.value = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
internal fun TabsContent(tabs: List<TabItem>, pagerState: PagerState) {
    HorizontalPager(state = pagerState) { page ->
        tabs[page].screen()
    }
}
