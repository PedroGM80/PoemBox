package dev.pgm.poembox.presentation.components

import android.widget.Toast
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import dev.pgm.poembox.domain.ContextContentProvider
import dev.pgm.poembox.presentation.MainActivity.Companion.VALIDATE_STATUS
import dev.pgm.poembox.presentation.components.TabItem.Editor.setUserData
import kotlinx.coroutines.launch

/**
 * Tabs
 *
 * @param tabs
 * @param pagerState
 * @param userData
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun Tabs(tabs: List<TabItem>, pagerState: PagerState, userData: String) {
    setUserData(userData)
    val scope = rememberCoroutineScope()
    // OR ScrollableTabRow()
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        backgroundColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onSecondary,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(Modifier.pagerTabIndicatorOffset(pagerState, tabPositions))
        }) {
        tabs.forEachIndexed { index, tab ->
            // OR Tab()
            LeadingIconTab(
                icon = { Icon(imageVector = tab.icon, contentDescription = tab.title) },
                text = { Text(tab.title) },
                selected = pagerState.currentPage == index,
                onClick = {
                    if (VALIDATE_STATUS == 0) {
                        Toast.makeText(
                            ContextContentProvider.applicationContext(),
                            "Please validate your draft",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    if (VALIDATE_STATUS == 1) {
                        Toast.makeText(
                            ContextContentProvider.applicationContext(),
                            "Please validate the analysis of the poem",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    scope.launch {
                        pagerState.animateScrollToPage(VALIDATE_STATUS)
                    }
                },
            )
        }
    }
}

