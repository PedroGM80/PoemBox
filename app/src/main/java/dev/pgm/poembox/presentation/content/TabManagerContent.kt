package dev.pgm.poembox.presentation.content

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pgm.poembox.R
import dev.pgm.poembox.presentation.theme.Dimens
import dev.pgm.poembox.presentation.components.TabItem
import dev.pgm.poembox.presentation.viewmodels.ManagerViewModel
import dev.pgm.poembox.presentation.viewmodels.SortOrder

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
    onViewNotes: () -> Unit,
    onEdit: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .padding(horizontal = Dimens.PaddingLarge, vertical = Dimens.SpacingSmall)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(Dimens.PaddingLarge),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = poem.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.manager_by_author, poem.author),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = poem.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = stringResource(R.string.manager_edit_description),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
                IconButton(onClick = onViewPoem) {
                    Icon(
                        Icons.Default.Description,
                        contentDescription = stringResource(R.string.manager_view_poem_description),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onViewNotes) {
                    Icon(
                        Icons.Default.Notes,
                        contentDescription = stringResource(R.string.manager_view_notes_description),
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.manager_delete_description),
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
    val searchQuery by viewModel.searchQuery
    val sortOrder by viewModel.sortOrder
    val context = LocalContext.current

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
                    contentDescription = stringResource(R.string.manager_refresh_description),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                placeholder = { Text(stringResource(R.string.manager_search_placeholder)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.PaddingLarge, vertical = Dimens.PaddingMedium),
                shape = MaterialTheme.shapes.medium
            )
            Row(
                modifier = Modifier.padding(horizontal = Dimens.PaddingLarge),
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium)
            ) {
                SortOrder.entries.forEach { order ->
                    val label = when (order) {
                        SortOrder.DATE_DESC -> stringResource(R.string.manager_sort_recent)
                        SortOrder.DATE_ASC -> stringResource(R.string.manager_sort_oldest)
                        SortOrder.TITLE_ASC -> stringResource(R.string.manager_sort_title)
                    }
                    FilterChip(
                        selected = sortOrder == order,
                        onClick = { viewModel.onSortOrderChange(order) },
                        label = { Text(label, style = MaterialTheme.typography.labelMedium) }
                    )
                }
            }
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    poems.isEmpty() -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoStories,
                                contentDescription = null,
                                modifier = Modifier.size(72.dp),
                                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                            Spacer(Modifier.height(Dimens.PaddingNormal))
                            Text(
                                text = stringResource(R.string.manager_empty_title),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = stringResource(R.string.manager_empty_subtitle),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = Dimens.PaddingNormal)
                        ) {
                            items(poems) { poem ->
                                PoemCard(
                                    poem = poem,
                                    onEdit = { viewModel.requestEditPoem(poem.title) },
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
                                        dialogTitle = context.getString(R.string.manager_notes_prefix, poem.title)
                                        dialogBody = poem.annotations.ifBlank { context.getString(R.string.manager_no_notes) }
                                        showContentDialog = true
                                    }
                                )
                            }
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
                    Text(stringResource(R.string.manager_close_button))
                }
            }
        )
    }

    poemToDelete?.let { poem ->
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    poemToDelete = null
                },
                title = { Text(stringResource(R.string.manager_delete_dialog_title)) },
                text = { Text(context.getString(R.string.manager_delete_dialog_text, poem.title)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deletePoem(poem)
                            showDeleteDialog = false
                            poemToDelete = null
                        }
                    ) {
                        Text(stringResource(R.string.manager_delete_dialog_confirm), color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        poemToDelete = null
                    }) {
                        Text(stringResource(R.string.manager_cancel_button))
                    }
                }
            )
        }
    }
}

@Composable
internal fun TabsContent(tabs: List<TabItem>, pagerState: PagerState) {
    HorizontalPager(state = pagerState) { page ->
        tabs[page].screen()
    }
}
