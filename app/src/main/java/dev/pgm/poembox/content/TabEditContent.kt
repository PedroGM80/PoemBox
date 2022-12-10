package dev.pgm.poembox.content

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.pgm.poembox.ContextContentProvider
import dev.pgm.poembox.MainActivity.Companion.POEM_TITLE
import dev.pgm.poembox.MainActivity.Companion.VALIDATE_STATUS
import dev.pgm.poembox.roomUtils.Draft
import dev.pgm.poembox.roomUtils.PoemBoxDatabase
import dev.pgm.poembox.ui.theme.ColorPoemField
import dev.pgm.poembox.ui.theme.Shapes
import dev.pgm.poembox.ui.theme.Typography
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun EditScreen(userData: String) {
    val maxChar = 30
    val custom = remember { mutableStateOf(Color.Blue) }
    Surface(color = MaterialTheme.colors.primary) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .wrapContentSize(Alignment.Center)
        ) {
            val scope = rememberCoroutineScope()

            Text(
                text = "Edit",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center,
                fontSize = 25.sp
            )

            var textTitle by remember { mutableStateOf(TextFieldValue("")) }
            var textContent by remember { mutableStateOf(TextFieldValue("")) }

            TextField(
                value = textTitle,

                label = { Text(text = "Title") },

                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),

                onValueChange = {
                    if (it.text.length <= maxChar) textTitle = it
                },textStyle = TextStyle(textAlign = TextAlign.Center),
                colors = ColorPoemField,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(10.dp),
                shape = Shapes.medium,

            )
            TextField(
                value = textContent,
                label = { Text(text = "") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                onValueChange = {
                    textContent = it
                },
                textStyle = TextStyle(fontSize = Typography.body2.fontSize,textAlign = TextAlign.Center),
                colors = ColorPoemField,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .align(Alignment.CenterHorizontally)
                    .height(400.dp),
                shape = Shapes.medium,
            )
            Button(
                onClick = {
                    if (custom.value == Color.Blue) {
                        val dataSplit = userData.split("#")
                        val userLoaded = dataSplit[1]
                        val draft = Draft(
                            title = textTitle.text,
                            draftContent = textContent.text,
                            writerName = userLoaded,
                            draftAnnotation = "",
                            writtenDate = getDate()
                        )
                        val titlePoem = draft.title
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                PoemBoxDatabase.getDatabase()?.draftDao()?.addDraft(draft)
                            }
                        }
                        POEM_TITLE = titlePoem
                        VALIDATE_STATUS = 1
                        custom.value = Color.Green
                    }else{
                        Toast.makeText(ContextContentProvider.applicationContext(),"Your poem is saved",Toast.LENGTH_LONG).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = custom.value),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(75.dp)
                    .padding(10.dp)
            ) {
                Text(
                    text = "Validate poem draft", fontSize = 15.sp
                )
            }
        }
    }
}

internal fun getDate(): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:MM:SS", Locale(Locale.ROOT.language))
    val date = Date()
    return formatter.format(date).toString()
}

@Preview(showBackground = true)
@Composable
fun EditScreenPreview() {
    EditScreen("mail#nombre")
}
