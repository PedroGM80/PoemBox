package dev.pgm.poembox.presentation.content

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.pgm.poembox.domain.ContextContentProvider
import dev.pgm.poembox.domain.UseCase
import dev.pgm.poembox.presentation.theme.ColorPoemFieldDialog
import dev.pgm.poembox.presentation.theme.Shapes
import dev.pgm.poembox.presentation.theme.Typography
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun inputDialogSample(title: String, bodyInput: String): Boolean {
    val openDialog = remember { mutableStateOf(true) }
    val mutableBody = remember { mutableStateOf(bodyInput) }
    val scope = rememberCoroutineScope()
    MaterialTheme {
        Column {

            if (openDialog.value) {
                AlertDialog(onDismissRequest = { openDialog.value = false }, title = {
                    Text(text = title)
                }, text = {
                    Column {
                        TextField(
                            value = mutableBody.value,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            onValueChange = { mutableBody.value = it },
                            textStyle = TextStyle(
                                fontSize = Typography.body2.fontSize,
                                textAlign = TextAlign.Center
                            ),
                            colors = ColorPoemFieldDialog,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .wrapContentHeight(),
                            shape = Shapes.medium,
                        )
                    }
                }, confirmButton = {
                    Button(onClick = {

                        scope.launch {
                            withContext(Dispatchers.IO) {
                                val useCase = UseCase()
                                val draft = useCase.findDraftByTitle(title)
                                if (draft != null) {

                                    draft.draftAnnotation = mutableBody.value
                                    Log.i(":::SAVENOTE", draft.toString())
                                    useCase.updateNoteByTitle(draft.draftAnnotation, draft.title)
                                }
                                Toast.makeText(
                                    ContextContentProvider.applicationContext(),
                                    "Save successful",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }) { Text("Save") }
                }, dismissButton = {
                    Button(onClick = { openDialog.value = false }) {
                        Text("Close")
                    }
                })
            }
        }
    }
    return openDialog.value
}

@Composable
fun alertDialogSample(title: String, body: String): Boolean {
    val openDialog = remember { mutableStateOf(true) }

    MaterialTheme {
        Column {

            if (openDialog.value) {

                AlertDialog(onDismissRequest = {

                    openDialog.value = false
                },
                    title = { Text(text = title) },
                    text = { Text(text = body) },
                    confirmButton = {
                        Button(onClick = { openDialog.value = false }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { openDialog.value = false }) {
                            Text("Close")
                        }
                    }
                )
            }
        }
    }
    return openDialog.value
}