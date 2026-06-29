package dev.pgm.poembox.presentation.content

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
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
import dev.pgm.poembox.presentation.util.PoemCardRenderer
import dev.pgm.poembox.presentation.util.Analytics
import dev.pgm.poembox.presentation.components.TabItem
import dev.pgm.poembox.presentation.viewmodels.ManagerViewModel
import dev.pgm.poembox.presentation.viewmodels.SortOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class PoemDetails(
    val title: String,
    val author: String,
    val date: String,
    val annotations: String,
    val poem: String
)

// Fullscreen Premium Poem Viewer supporting custom wallpapers and direct social sharing
@Composable
fun PoemViewerDialog(
    title: String,
    body: String,
    onDismiss: () -> Unit,
    context: android.content.Context = LocalContext.current
) {
    var bgUri by remember { mutableStateOf<Uri?>(null) }
    var warmBackground by remember { mutableStateOf(false) }
    val bgColor = if (warmBackground) ImmersiveWarmBackground else ImmersiveDarkBackground
    val textColor = if (warmBackground) ImmersiveWarmText else ImmersiveDarkText
    var showShareMenu by remember { mutableStateOf(false) }

    val bgImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> bgUri = uri }

    val bitmapState = remember(bgUri) { mutableStateOf<android.graphics.Bitmap?>(null) }
    LaunchedEffect(bgUri) {
        if (bgUri != null) {
            withContext(Dispatchers.IO) {
                runCatching {
                    context.contentResolver.openInputStream(bgUri!!)?.use {
                        android.graphics.BitmapFactory.decodeStream(it)
                    }
                }.getOrNull()
            }?.let {
                bitmapState.value = it
            }
        } else {
            bitmapState.value = null
        }
    }

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
            // Background Image
            if (bitmapState.value != null) {
                Image(
                    bitmap = bitmapState.value!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.25f
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Dimens.PaddingExtraLarge)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(80.dp))
                Text(
                    text = title,
                    fontFamily = PoeticFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
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

            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(Dimens.PaddingSmall),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = textColor
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { bgImagePickerLauncher.launch("image/*") }) {
                        Icon(
                            Icons.Default.Wallpaper,
                            contentDescription = "Elegir fondo",
                            tint = if (bgUri != null) MaterialTheme.colorScheme.secondary else textColor
                        )
                    }
                    Box {
                        IconButton(onClick = { showShareMenu = true }) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = "Compartir",
                                tint = textColor
                            )
                        }
                        DropdownMenu(
                            expanded = showShareMenu,
                            onDismissRequest = { showShareMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Compartir como texto") },
                                leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) },
                                onClick = {
                                    showShareMenu = false
                                    val shareText = "$title\n\n$body"
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                        putExtra(Intent.EXTRA_SUBJECT, title)
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Compartir poema"))
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Compartir como imagen") },
                                leadingIcon = { Icon(Icons.Default.Wallpaper, contentDescription = null) },
                                onClick = {
                                    showShareMenu = false
                                    val uri = PoemCardRenderer.createAndShare(
                                        context, title, body,
                                        darkMode = true,
                                        backgroundImageUri = bgUri
                                    )
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "image/png"
                                        putExtra(Intent.EXTRA_STREAM, uri)
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Compartir tarjeta de poema"))
                                }
                            )
                        }
                    }
                    Spacer(Modifier.width(Dimens.SpacingSmall))
                    Switch(
                        checked = warmBackground,
                        onCheckedChange = { warmBackground = it },
                        modifier = Modifier.padding(end = Dimens.PaddingSmall)
                    )
                }
            }
        }
    }
}

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
        Column(
            modifier = Modifier.padding(Dimens.PaddingLarge)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = poem.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                // Solo mostramos el autor si existe (la app no obliga a indicarlo).
                val unknownAuthor = stringResource(R.string.editor_unknown_author)
                if (poem.author.isNotBlank() && poem.author != unknownAuthor) {
                    Spacer(Modifier.height(Dimens.SpacingSmall))
                    Text(
                        text = stringResource(R.string.manager_by_author, poem.author),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(Dimens.SpacingSmall))
                Text(
                    text = poem.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Spacer(Modifier.height(Dimens.SpacingSmall))
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
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
    var showContentDialog by remember { mutableStateOf(false) }
    var showPoemViewerDialog by remember { mutableStateOf(false) }
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
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
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
                                val notesPrefixText = stringResource(R.string.manager_notes_prefix, poem.title)
                                val noNotesText = stringResource(R.string.manager_no_notes)
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
                                        showPoemViewerDialog = true
                                    },
                                    onViewNotes = {
                                        dialogTitle = notesPrefixText
                                        dialogBody = poem.annotations.ifBlank { noNotesText }
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

    if (showPoemViewerDialog) {
        PoemViewerDialog(
            title = dialogTitle,
            body = dialogBody,
            onDismiss = { showPoemViewerDialog = false }
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
                text = { Text(stringResource(R.string.manager_delete_dialog_text, poem.title)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            Analytics.poemDeleted()
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
