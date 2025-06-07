package dev.pgm.poembox.presentation.components

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import dev.pgm.poembox.presentation.MainActivity.Companion.VALIDATE_STATUS
import dev.pgm.poembox.presentation.components.TabItem.Editor.setUserData
import kotlinx.coroutines.launch

/**
 * Tabs component for navigation
 *
 * @param tabs List of tabs to display
 * @param pagerState State for controlling the pager
 * @param userData User data to be set
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Tabs(
    tabs: List<TabItem>,
    pagerState: PagerState,
    userData: String,
    modifier: Modifier = Modifier
) {
    setUserData(userData)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    TabRow(
        selectedTabIndex = pagerState.currentPage,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        indicator = { tabPositions ->
            if (tabPositions.isNotEmpty()) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage])
                )
            }
        }
    ) {
        tabs.forEachIndexed { index, tab ->
            Tab(
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title
                    )
                },
                text = {
                    Text(
                        text = tab.title,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                selected = pagerState.currentPage == index,
                onClick = {
                    when (VALIDATE_STATUS) {
                        0 -> {
                            Toast.makeText(
                                context,
                                "Please validate your draft",
                                Toast.LENGTH_LONG
                            ).show()
                            scope.launch {
                                pagerState.animateScrollToPage(0)
                            }
                        }
                        1 -> {
                            Toast.makeText(
                                context,
                                "Please validate the analysis of the poem",
                                Toast.LENGTH_LONG
                            ).show()
                            scope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        }
                        else -> {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    }
                },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}