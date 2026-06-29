package dev.pgm.poembox.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.pgm.poembox.presentation.components.TabItem
import dev.pgm.poembox.presentation.components.TopBar
import dev.pgm.poembox.presentation.content.TabsContent
import dev.pgm.poembox.presentation.viewmodels.ScreenTabsViewModel
import kotlinx.coroutines.launch

private const val EXPANDED_WIDTH_DP = 600

@Composable
fun ScreenTabs(
    navController: NavController,
    viewModel: ScreenTabsViewModel = hiltViewModel()
) {
    val tabs = remember { listOf(TabItem.Editor, TabItem.Monitor, TabItem.Manager) }
    val pagerState = rememberPagerState { tabs.size }
    val pendingEditTitle by viewModel.pendingEditTitle.collectAsState()
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val isExpanded = configuration.screenWidthDp >= EXPANDED_WIDTH_DP

    LaunchedEffect(pendingEditTitle) {
        if (pendingEditTitle.isNotBlank()) {
            pagerState.animateScrollToPage(0)
        }
    }

    val onLogout: () -> Unit = {
        navController.navigate(ScreensRouteList.RouteScreenCreateAccount.route) {
            popUpTo(0) { inclusive = true }
        }
    }

    if (isExpanded) {
        // ── Tablet / foldable: NavigationRail on the left ─────────────────
        Scaffold(
            topBar = { TopBar(onLogout = onLogout) }
        ) { padding ->
            Row(modifier = Modifier.padding(padding).fillMaxSize()) {
                NavigationRail(
                    modifier = Modifier.fillMaxHeight(),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    tabs.forEachIndexed { index, tab ->
                        val selected = pagerState.currentPage == index
                        NavigationRailItem(
                            selected = selected,
                            onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = stringResource(tab.title)
                                )
                            },
                            label = { Text(stringResource(tab.title)) }
                        )
                    }
                }
                // Content takes the remaining width
                Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    TabsContent(tabs = tabs, pagerState = pagerState)
                }
            }
        }
    } else {
        // ── Phone / compact: Material 3 NavigationBar ─────────────────────
        Scaffold(
            topBar = { TopBar(onLogout = onLogout) },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ) {
                    tabs.forEachIndexed { index, tab ->
                        val selected = pagerState.currentPage == index
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                scope.launch { pagerState.animateScrollToPage(index) }
                            },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = stringResource(tab.title)
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(tab.title),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            alwaysShowLabel = true,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                TabsContent(tabs = tabs, pagerState = pagerState)
            }
        }
    }
}
