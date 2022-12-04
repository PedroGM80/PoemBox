package dev.pgm.poembox.content

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
import dev.pgm.poembox.roomUtils.Draft
import dev.pgm.poembox.roomUtils.PoemBoxDatabase
import dev.pgm.poembox.ui.theme.ColorPoemTitleField
import dev.pgm.poembox.ui.theme.Shapes
import dev.pgm.poembox.ui.theme.Typography


@Composable
fun EditScreen() {
    Surface(color = MaterialTheme.colors.primary) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .wrapContentSize(Alignment.Center)
        ) {
            Text(
                text = "Edit",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onBackground,
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
                    textTitle = it
                },
                colors = ColorPoemTitleField,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                shape = Shapes.medium
            )
            TextField(
                value = textContent,
                label = { Text(text = "") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                onValueChange = {
                    textContent = it
                },
                textStyle = TextStyle(fontSize = Typography.body2.fontSize),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .fillMaxHeight(), // Here I have decreased the height
                shape = Shapes.medium,
            )
            Button(
                onClick = {
                   // val draft=Draft(textTitle.text,textContent.text,"",,)
                 //   PoemBoxDatabase().draftDao().addDraft(draft)
                    },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(75.dp)
                    .padding(10.dp)
            ) {
                Text(
                    text = "Validate poem draft",
                    fontSize = 15.sp
                )
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun EditScreenPreview() {
    EditScreen()
}