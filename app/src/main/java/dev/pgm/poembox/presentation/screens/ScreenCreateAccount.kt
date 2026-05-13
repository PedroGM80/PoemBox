package dev.pgm.poembox.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.pgm.poembox.R
import dev.pgm.poembox.presentation.viewmodels.AuthViewModel

@Composable
fun CreateAccount(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var userInputName by remember { mutableStateOf("") }
    var userInputMail by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.create_account_title),
            style = MaterialTheme.typography.headlineLarge,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            label = { Text(stringResource(R.string.create_account_username_label)) },
            value = userInputName,
            onValueChange = { userInputName = it },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            label = { Text(stringResource(R.string.create_account_email_label)) },
            value = userInputMail,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            onValueChange = { userInputMail = it },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (userInputName.isBlank()) return@Button
                if (verifyEmail(userInputMail)) {
                    viewModel.registerUser(userInputName.trim(), userInputMail.trim()) {
                        navController.navigate(ScreensRouteList.RouteScreenTabs.route) {
                            popUpTo(0)
                        }
                    }
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.create_account_invalid_email),
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            enabled = userInputName.isNotBlank() && userInputMail.isNotBlank(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(text = stringResource(R.string.create_account_button), fontSize = 18.sp)
        }
    }
}

fun verifyEmail(mail: String): Boolean {
    val pattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9-]+\\.[a-zA-Z.]{2,18}".toRegex()
    return pattern.matches(mail)
}
