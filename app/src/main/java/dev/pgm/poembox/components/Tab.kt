package dev.pgm.poembox.components

import android.util.Log
import android.widget.Toast
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import dev.pgm.poembox.ContextContentProvider
import dev.pgm.poembox.MainActivity.Companion.VALIDATE_STATUS
import dev.pgm.poembox.components.TabItem.Editor.setUserData
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Tabs(tabs: List<TabItem>, pagerState: PagerState, userData: String) {
    setUserData(userData)
    val scope = rememberCoroutineScope()
    // OR ScrollableTabRow()
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        backgroundColor = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.onSecondary,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(Modifier.pagerTabIndicatorOffset(pagerState, tabPositions))
        }) {
        tabs.forEachIndexed { index, tab ->
            // OR Tab()
            LeadingIconTab(
                icon = { Icon(painter = painterResource(id = tab.icon), contentDescription = "") },
                text = { Text(tab.title) },
                selected = pagerState.currentPage == index,
                onClick = {
                    if(VALIDATE_STATUS==0){
                        Toast.makeText(ContextContentProvider.applicationContext(),"Please validate your draft",Toast.LENGTH_LONG).show()
                    }
                    if(VALIDATE_STATUS==1){
                        Toast.makeText(ContextContentProvider.applicationContext(),"Please validate the analysis of the poem",Toast.LENGTH_LONG).show()
                    }
                    scope.launch {
                        pagerState.animateScrollToPage(VALIDATE_STATUS)
                        Log.i(":::INDEX",index.toString())
                    }
                },
            )
        }
    }
}

