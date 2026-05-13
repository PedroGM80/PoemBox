package dev.pgm.poembox.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pgm.poembox.R
import dev.pgm.poembox.presentation.viewmodels.AuthViewModel
import dev.pgm.poembox.presentation.viewmodels.StatsViewModel

@Composable
private fun StatItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatsBottomSheet(
    onDismiss: () -> Unit,
    statsViewModel: StatsViewModel = hiltViewModel()
) {
    val stats by statsViewModel.stats.collectAsState()
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(Unit) { statsViewModel.load() }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.stats_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))
            StatItem(stringResource(R.string.stats_total_drafts), stats.totalDrafts.toString())
            StatItem(stringResource(R.string.stats_validated), stats.validatedPoems.toString())
            StatItem(stringResource(R.string.stats_total_words), "${stats.totalWords} ${stringResource(R.string.stats_words_unit)}")
            if (stats.longestPoemTitle.isNotBlank()) {
                StatItem(stringResource(R.string.stats_longest_poem), "${stats.longestPoemWords} ${stringResource(R.string.stats_words_unit)}")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "\"${stats.longestPoemTitle}\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    onLogout: (() -> Unit)? = null,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val userName by authViewModel.userName.collectAsState()
    var showMenu by remember { mutableStateOf(false) }
    var showStats by remember { mutableStateOf(false) }

    if (showStats) {
        StatsBottomSheet(onDismiss = { showStats = false })
    }

    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        actions = {
            if (!userName.isNullOrBlank()) {
                Text(
                    text = userName ?: "",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 8.dp)
                )
                IconButton(onClick = { showStats = true }) {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = stringResource(R.string.topbar_stats_description),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = stringResource(R.string.user_menu_description),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.logout)) },
                            onClick = {
                                showMenu = false
                                authViewModel.logout { onLogout?.invoke() }
                            }
                        )
                    }
                }
            }
        },
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}
