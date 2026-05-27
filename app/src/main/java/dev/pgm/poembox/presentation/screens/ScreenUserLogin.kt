package dev.pgm.poembox.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.pgm.poembox.R
import dev.pgm.poembox.presentation.viewmodels.AuthViewModel

import dev.pgm.poembox.presentation.theme.Dimens

@Composable
fun UserLogin(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val userName by viewModel.userName.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
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
                text = stringResource(R.string.login_title),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimens.PaddingNormal))

            Text(
                text = stringResource(R.string.login_logged_as, userName ?: ""),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimens.ButtonHeight))

            Button(
                onClick = {
                    navController.navigate(ScreensRouteList.RouteScreenTabs.route) {
                        popUpTo(0)
                    }
                },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.ButtonHeight)
            ) {
                Text(
                    text = stringResource(R.string.login_enter_button),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
