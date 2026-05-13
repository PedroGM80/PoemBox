package dev.pgm.poembox.presentation.content

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.pgm.poembox.presentation.theme.Shapes
import dev.pgm.poembox.presentation.theme.Typography
import dev.pgm.poembox.presentation.viewmodels.AuthViewModel
import dev.pgm.poembox.presentation.viewmodels.EditViewModel

@Composable
fun EditScreen(
    viewModel: EditViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val title by viewModel.title
    val content by viewModel.content
    val analysisResult by viewModel.analysisResult
    val isSaved by viewModel.isSaved
    val userName by authViewModel.userName.collectAsState()
    val context = LocalContext.current

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = title,
                    onValueChange = { viewModel.onTitleChange(it) },
                    label = { Text(text = "Título del poema") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    textStyle = TextStyle(
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    shape = Shapes.medium,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
                IconButton(onClick = { viewModel.clearPoem() }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Nuevo poema",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                TextField(
                    value = content,
                    onValueChange = { viewModel.onContentChange(it) },
                    placeholder = { Text(text = "Escribe aquí tu poema...") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    textStyle = TextStyle(
                        fontSize = Typography.bodyLarge.fontSize,
                        lineHeight = 28.sp
                    ),
                    modifier = Modifier.fillMaxSize(),
                    shape = Shapes.medium,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
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
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Button(
                onClick = {
                    viewModel.saveDraft(userName ?: "Desconocido") {
                        Toast.makeText(context, "Borrador guardado", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = title.isNotBlank() && content.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSaved) MaterialTheme.colorScheme.secondary
                    else MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(56.dp)
            ) {
                Text(
                    text = if (isSaved) "Borrador guardado ✓" else "Guardar borrador",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
