package dev.pgm.poembox.presentation.content

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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.pgm.poembox.R
import dev.pgm.poembox.domain.ContextContentProvider
import dev.pgm.poembox.domain.UseCase
import dev.pgm.poembox.presentation.MainActivity.Companion.POEM_TITLE
import dev.pgm.poembox.presentation.MainActivity.Companion.VALIDATE_STATUS
import dev.pgm.poembox.presentation.theme.ColorPoemField
import dev.pgm.poembox.presentation.theme.Shapes
import dev.pgm.poembox.presentation.theme.Typography
import dev.pgm.poembox.repository.Draft
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EditScreen(userData: String) {
    val maxChar = 60
    val custom = remember { mutableStateOf(Color.Blue) }
    Surface(color = MaterialTheme.colors.primary) {
        Box(Modifier.wrapContentSize(Alignment.Center)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(id = R.color.white))
                    .align(Alignment.TopCenter)
            ) {
                val scope = rememberCoroutineScope()
                var textTitle by remember { mutableStateOf(TextFieldValue("")) }
                var textContent by remember { mutableStateOf(TextFieldValue("")) }

                TextField(
                    value = textTitle,
                    label = { Text(text = "Title") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    onValueChange = {
                        if (it.text.length <= maxChar) textTitle = it
                    },
                    textStyle = TextStyle(textAlign = TextAlign.Center),
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
                    textStyle = TextStyle(
                        fontSize = Typography.body2.fontSize,
                        textAlign = TextAlign.Center
                    ),
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
                                    UseCase().addDraft(draft)
                                }
                            }
                            POEM_TITLE = titlePoem
                            VALIDATE_STATUS = 1
                            custom.value = Color.Green
                        } else {
                            Toast.makeText(
                                ContextContentProvider.applicationContext(),
                                "Your poem is saved",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = custom.value),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Validate poem draft", fontSize = 15.sp
                    )
                }
            }
        }
    }
}

/**
 * Get date
 *
 * @return actual date
 */
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
