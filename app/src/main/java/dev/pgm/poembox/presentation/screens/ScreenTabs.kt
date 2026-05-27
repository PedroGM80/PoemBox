package dev.pgm.poembox.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.pgm.poembox.presentation.components.TabItem
import dev.pgm.poembox.presentation.components.Tabs
import dev.pgm.poembox.presentation.components.TopBar
import dev.pgm.poembox.presentation.content.TabsContent
import dev.pgm.poembox.presentation.viewmodels.ScreenTabsViewModel

@Composable
fun ScreenTabs(
    navController: NavController,
    viewModel: ScreenTabsViewModel = hiltViewModel()
) {
    val tabs = remember { listOf(TabItem.Editor, TabItem.Monitor, TabItem.Manager) }
    val pagerState = rememberPagerState { tabs.size }
    val pendingEditTitle by viewModel.pendingEditTitle.collectAsState()

    LaunchedEffect(pendingEditTitle) {
        if (pendingEditTitle.isNotBlank()) {
            pagerState.animateScrollToPage(0)
        }
    }

    Scaffold(
        topBar = {
            TopBar(onLogout = {
                navController.navigate(ScreensRouteList.RouteScreenCreateAccount.route) {
                    popUpTo(0) { inclusive = true }
                }
            })
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp, top = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.indices.forEach { index ->
                    val selected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (selected) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            )
                    )
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
