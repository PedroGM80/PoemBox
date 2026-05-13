package dev.pgm.poembox.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.pgm.poembox.presentation.components.Logo
import dev.pgm.poembox.presentation.viewmodels.AuthViewModel
import kotlinx.coroutines.delay

private const val SPLASH_DELAY_MS = 1500L

@Composable
fun ScreenSplash(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        delay(SPLASH_DELAY_MS)
        if (viewModel.userName.value != null) {
            navController.navigate(ScreensRouteList.RouteScreenLogin.route) {
                popUpTo(0)
            }
        } else {
            navController.navigate(ScreensRouteList.RouteScreenCreateAccount.route) {
                popUpTo(0)
            }
        }
    }
    Logo()
}
