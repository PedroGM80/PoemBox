package dev.pgm.poembox.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.pgm.poembox.R
import dev.pgm.poembox.domain.Constants
import dev.pgm.poembox.presentation.theme.Dimens
import dev.pgm.poembox.presentation.viewmodels.AuthViewModel

@Composable
fun CreateAccount(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var userInputName by remember { mutableStateOf("") }
    var userInputMail by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .imePadding()
                .padding(Dimens.PaddingExtraLarge),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.brand_name),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

            HorizontalDivider(
                modifier = Modifier.width(Dimens.DividerWidthSmall),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
            )

            Spacer(modifier = Modifier.height(Dimens.PaddingExtraLarge))

            Text(
                text = stringResource(R.string.create_account_title),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimens.PaddingExtraLarge))

            OutlinedTextField(
                label = { Text(stringResource(R.string.create_account_username_label)) },
                value = userInputName,
                onValueChange = { userInputName = it },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Dimens.PaddingLarge))

            OutlinedTextField(
                label = { Text(stringResource(R.string.create_account_email_label)) },
                value = userInputMail,
                onValueChange = {
                    userInputMail = it
                    if (emailError) emailError = false
                },
                leadingIcon = {
                    Icon(Icons.Default.AlternateEmail, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = emailError,
                supportingText = {
                    if (emailError) {
                        Text(
                            text = stringResource(R.string.create_account_invalid_email),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Dimens.PaddingExtraLarge))

            Button(
                onClick = {
                    if (userInputName.isBlank()) return@Button
                    if (verifyEmail(userInputMail)) {
                        emailError = false
                        viewModel.registerUser(userInputName.trim(), userInputMail.trim()) {
                            navController.navigate(ScreensRouteList.RouteScreenTabs.route) {
                                popUpTo(0)
                            }
                        }
                    } else {
                        emailError = true
                    }
                },
                enabled = userInputName.isNotBlank() && userInputMail.isNotBlank(),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.ButtonHeight)
            ) {
                Text(
                    text = stringResource(R.string.create_account_button),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(Dimens.PaddingNormal))

            val guestName = stringResource(R.string.create_account_guest_name)
            TextButton(
                onClick = {
                    viewModel.registerUser(guestName, "") {
                        navController.navigate(ScreensRouteList.RouteScreenTabs.route) {
                            popUpTo(0)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.create_account_guest_button),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

private fun verifyEmail(mail: String): Boolean {
    val pattern = Constants.REGEX_EMAIL.toRegex()
    return pattern.matches(mail)
}
