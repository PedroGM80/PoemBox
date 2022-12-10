package dev.pgm.poembox.content

import android.util.Log
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import dev.pgm.poembox.R
import dev.pgm.poembox.components.TabItem
import dev.pgm.poembox.roomUtils.PoemBoxDatabase
import kotlinx.coroutines.launch

data class PoemDetails(
    var title: String,
    var author: String,
    var date: String,
    var annotations: String,
    var poem: String
) {
    override fun toString(): String {
        return "PoemDetails(title='$title', author='$author', date='$date', annotations='$annotations', poem='$poem')"
    }

}

@Composable
fun PoemCard(poem: PoemDetails) {
    val bodyDialog = remember { mutableStateOf("") }
    val titleDialog = remember { mutableStateOf("") }
    val showDialog = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val showCard = remember { mutableStateOf(1f) }
    Card(

        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .alpha(showCard.value)
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
                Row {
                    Image(

                        painter = painterResource(R.drawable.ic_baseline_text_snippet_24),
                        contentDescription = "Icon poem.",
                        contentScale = ContentScale.FillHeight,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(25.dp)
                            .clip((CircleShape))
                            .clickable(
                                true,
                                "Clickable image",
                                onClick = {
                                    titleDialog.value = poem.title
                                    bodyDialog.value = poem.poem
                                    showDialog.value = true
                                }
                            )
                    )
                    Image(
                        painter = painterResource(R.drawable.ic_baseline_notes_24),
                        contentDescription = "Icon annotations.",
                        contentScale = ContentScale.FillHeight,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(25.dp)
                            .clip((CircleShape))
                            .clickable(
                                true,
                                "Clickable image",
                                onClick = {
                                    titleDialog.value = buildString {
                                        append(poem.title)
                                        append(" annotations")
                                    }
                                    bodyDialog.value = poem.annotations
                                    showDialog.value = true
                                }
                            )
                    )
                    Image(
                        painter = painterResource(R.drawable.ic_baseline_delete_24),
                        contentDescription = "Icon remove.",
                        contentScale = ContentScale.FillHeight,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(25.dp)
                            .clip((CircleShape))
                            .clickable(
                                true,
                                "Clickable image",
                                onClick = {
                                    scope.launch {
                                        val dataBase = PoemBoxDatabase.getDatabase()
                                        val sheet =
                                            dataBase
                                                ?.sheetDao()
                                                ?.findByDateCreation(poem.date)
                                        val draft = dataBase
                                            ?.draftDao()
                                            ?.findByTitle(poem.title)
                                        if (sheet != null) {
                                            dataBase
                                                .sheetDao()
                                                .deleteSheet(sheet)
                                        }

                                        if (draft != null) {
                                            dataBase
                                                .draftDao()
                                                .deleteDraft(draft)
                                        }
                                    }
                                    titleDialog.value = buildString {
                                        append(poem.title)
                                        append(" deleted")
                                    }
                                    bodyDialog.value = "Delete successful."
                                  //  listener.delete(poem)
                                    showDialog.value = true
                                }
                            )
                    )
                }
            }
        }
    }
    if (showDialog.value) {
        showCard.value=0f
        showDialog.value = alertDialogSample(titleDialog.value, bodyDialog.value)
    }
}


@Composable
fun DetailsContent(poemList: MutableList<PoemDetails>) {

   // val poems = remember { poemList }
    if(poemList.isNotEmpty()){
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {

            items(
                poemList
            ) {
                PoemCard(poem = it)
            }
        }
    }
}

@Composable
fun ManagerScreen() {

    val list = remember {
        mutableListOf<PoemDetails>()
    }
    val showList = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Surface(color = MaterialTheme.colors.primary) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .wrapContentSize(Alignment.Center)
        ) {

        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.white))
                .wrapContentSize(Alignment.Center)
        ) {

            Text(
                text = "Manage",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center,
                fontSize = 25.sp
            )
            Button(
                onClick = {
                    scope.launch {
                        val dataBase = PoemBoxDatabase.getDatabase()
                        val sheets = dataBase?.sheetDao()?.getAllSheet()
                        Log.i(":::SHEETS", sheets.toString())
                        if (sheets != null) {
                            for (sheet in sheets) {
                                val draft = dataBase.draftDao().findByTitle(sheet.refDraftValidate)
                                if (draft != null) {
                                    Log.i(":::DRAFT", draft.toString())
                                    val poemDetail = PoemDetails(
                                        draft.title, draft.writerName,
                                        sheet.dateValidation, "", draft.draftContent
                                    )
                                    list.add(poemDetail)
                                    Log.i(":::POEM_DETAIL", poemDetail.toString())
                                    showList.value = true
                                }
                            }
                        }
                    }
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .wrapContentSize()
                    .padding(5.dp)
                    .align(Alignment.CenterHorizontally)
                    .background(Color.Transparent)
            ) { Text(text = "Charge poem") }
            Box {
                if (showList.value) {
                    DetailsContent(poemList = list)
                }
            }
        }
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
