package dev.pgm.poembox.presentation.content

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
            .padding(horizontal = 16.dp, vertical = 6.dp)
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
                    text = "Por ${poem.author}",
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
                    Icon(Icons.Default.Description, contentDescription = "Ver poema")
                }
                IconButton(onClick = onViewNotes) {
                    Icon(Icons.Default.Notes, contentDescription = "Ver notas")
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
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

    var showContentDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogBody by remember { mutableStateOf("") }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var poemToDelete by remember { mutableStateOf<PoemDetails?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadPoems()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.loadPoems() },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Actualizar",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                poems.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📜",
                            style = MaterialTheme.typography.displayMedium
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "Aún no tienes poemas validados.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Escribe y valida uno en la pestaña Analizar.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        items(poems) { poem ->
                            PoemCard(
                                poem = poem,
                                onDelete = {
                                    poemToDelete = poem
                                    showDeleteDialog = true
                                },
                                onViewPoem = {
                                    dialogTitle = poem.title
                                    dialogBody = poem.poem
                                    showContentDialog = true
                                },
                                onViewNotes = {
                                    dialogTitle = "Notas: ${poem.title}"
                                    dialogBody = poem.annotations.ifBlank { "Sin notas." }
                                    showContentDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showContentDialog) {
        AlertDialog(
            onDismissRequest = { showContentDialog = false },
            title = { Text(dialogTitle, style = MaterialTheme.typography.titleLarge) },
            text = {
                Text(
                    dialogBody,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                )
            },
            confirmButton = {
                TextButton(onClick = { showContentDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }

    if (showDeleteDialog && poemToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                poemToDelete = null
            },
            title = { Text("¿Eliminar poema?") },
            text = { Text("Se eliminará \"${poemToDelete!!.title}\" de forma permanente.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deletePoem(poemToDelete!!)
                        showDeleteDialog = false
                        poemToDelete = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    poemToDelete = null
                }) {
                    Text("Cancelar")
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
