package dev.pgm.poembox.presentation.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.pgm.poembox.presentation.components.TabItem
import dev.pgm.poembox.presentation.components.Tabs
import dev.pgm.poembox.presentation.components.TopBar
import dev.pgm.poembox.presentation.content.TabsContent
import dev.pgm.poembox.presentation.viewmodels.ScreenTabsViewModel
import dev.pgm.poembox.presentation.theme.Dimens
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
        // ── Phone / compact: bottom dot indicators ────────────────────────
        Scaffold(
            topBar = { TopBar(onLogout = onLogout) },
            bottomBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(bottom = Dimens.PaddingLarge, top = Dimens.PaddingMedium),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tabs.indices.forEach { index ->
                        val selected = pagerState.currentPage == index
                        val tabName = stringResource(tabs[index].title)
                        val dotColor by animateColorAsState(
                            targetValue = if (selected) MaterialTheme.colorScheme.primary
                                          else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                            label = "dot_color_$index"
                        )
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(48.dp)
                                .semantics {
                                    role = Role.Tab
                                    contentDescription = tabName
                                }
                                .clickable {
                                    scope.launch { pagerState.animateScrollToPage(index) }
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(if (selected) Dimens.PagerIndicatorSizeSelected else Dimens.PagerIndicatorSizeUnselected)
                                    .clip(CircleShape)
                                    .background(dotColor)
                                    .animateContentSize()
                            )
                        }
                    }
                }
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                Tabs(tabs = tabs, pagerState = pagerState)
                TabsContent(tabs = tabs, pagerState = pagerState)
            }
        }
    }
}
