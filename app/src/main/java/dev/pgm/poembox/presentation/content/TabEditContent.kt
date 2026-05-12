package dev.pgm.poembox.presentation.content

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pgm.poembox.R
import dev.pgm.poembox.domain.ContextContentProvider
import dev.pgm.poembox.presentation.viewmodels.EditViewModel
import dev.pgm.poembox.presentation.theme.Shapes
import dev.pgm.poembox.presentation.theme.Typography

@Composable
fun EditScreen(
    userData: String,
    viewModel: EditViewModel = hiltViewModel()
) {
    val title by viewModel.title
    val content by viewModel.content
    val analysisResult by viewModel.analysisResult
    var isSaved by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = title,
                onValueChange = { viewModel.onTitleChange(it) },
                label = { Text(text = "Poem Title") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                textStyle = TextStyle(
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = Shapes.medium,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                TextField(
                    value = content,
                    onValueChange = { viewModel.onContentChange(it) },
                    placeholder = { Text(text = "Start writing your poem here...") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    textStyle = TextStyle(
                        fontSize = Typography.bodyLarge.fontSize,
                        lineHeight = 24.sp
                    ),
                    modifier = Modifier.fillMaxSize(),
                    shape = Shapes.medium,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colorResource(id = R.color.white).copy(alpha = 0.5f),
                        unfocusedContainerColor = colorResource(id = R.color.white).copy(alpha = 0.3f)
                    )
                )

                if (analysisResult.isNotBlank()) {
                    Text(
                        text = analysisResult,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Button(
                onClick = {
                    val dataSplit = userData.split("#")
                    val userLoaded = if (dataSplit.size > 1) dataSplit[1] else "Unknown"
                    viewModel.saveDraft(userLoaded) {
                        isSaved = true
                        Toast.makeText(
                            ContextContentProvider.applicationContext(),
                            "Poem draft saved successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSaved) Color.Gray else MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(56.dp)
            ) {
                Text(
                    text = if (isSaved) "Draft Saved" else "Save Poem Draft",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
