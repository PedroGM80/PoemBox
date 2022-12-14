package dev.pgm.poembox.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import dev.pgm.poembox.presentation.components.TabItem
import dev.pgm.poembox.presentation.components.Tabs
import dev.pgm.poembox.presentation.components.TopBar
import dev.pgm.poembox.presentation.content.TabsContent


/**
 * Screen tabs
 *
 * @param userData
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun ScreenTabs(userData: String) {
    val tabs: List<TabItem> = listOf(TabItem.Editor, TabItem.Monitor, TabItem.Manager)
    val pagerState = rememberPagerState()
    Scaffold(
        topBar = { TopBar() },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
        ) {
            Tabs(tabs = tabs, pagerState = pagerState, userData = userData)
            TabsContent(tabs = tabs, pagerState = pagerState)
        }
    }
}

