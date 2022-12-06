package dev.pgm.poembox.content

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import dev.pgm.poembox.R
import dev.pgm.poembox.components.TabItem

data class PoemDetails(
    val title: String,
    val author: String,
    val date: String,
    val annotations: String,
    val poem: String
)

@Composable
fun PoemCard(poem: PoemDetails) {
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .fillMaxWidth(),
        elevation = 2.dp,
        backgroundColor = Color.LightGray,
        shape = RoundedCornerShape(corner = CornerSize(16.dp))

    ) {

        Row(modifier = Modifier.padding(20.dp)) {
            Column(
                modifier = Modifier.weight(1f),
                Arrangement.Center
            ) {
                Text(
                    text = poem.title,
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    )
                )
                Text(
                    text = "Author: " + poem.author,
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 15.sp
                    )
                )
                Row() {
                    Image(
                        painter = painterResource(R.drawable.ic_baseline_text_snippet_24),
                        contentDescription = "Icon poem.",
                        contentScale = ContentScale.FillHeight,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(110.dp)
                            .clip((CircleShape))
                    )
                    Image(
                        painter = painterResource(R.drawable.ic_baseline_notes_24),
                        contentDescription = "Icon annotations.",
                        contentScale = ContentScale.FillHeight,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(110.dp)
                            .clip((CircleShape))
                    )
                    Image(
                        painter = painterResource(R.drawable.ic_baseline_delete_24),
                        contentDescription = "Icon remove.",
                        contentScale = ContentScale.FillHeight,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(110.dp)
                            .clip((CircleShape))
                    )
                }
            }


        }
    }
}

@Composable
fun DetailsContent(poemList:List<PoemDetails>) {

    val poems = remember { poemList}
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(
            poems
        ) {
            PoemCard(poem = it)
        }
    }
}

@Composable
fun ManagerScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.white))
            .wrapContentSize(Alignment.Center)
    ) {
/*
        Text(
            text = "Manage",
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 25.sp
        )*/

    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
internal fun TabsContent(tabs: List<TabItem>, pagerState: PagerState) {
    HorizontalPager(state = pagerState, count = tabs.size) { page ->
        tabs[page].screen()
    }
}

@Preview(showBackground = true)
@Composable
fun ManagerScreenPreview() {
    ManagerScreen()
}
