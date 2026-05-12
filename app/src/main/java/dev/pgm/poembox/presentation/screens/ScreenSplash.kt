package dev.pgm.poembox.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.pgm.poembox.presentation.components.Logo
import dev.pgm.poembox.presentation.viewmodels.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun ScreenSplash(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val userName by viewModel.userName.collectAsState()

    LaunchedEffect(key1 = userName) {
        delay(1500)
        if (userName == null) {
            navController.navigate(ScreensRouteList.RouteScreenCreateAccount.route) {
                popUpTo(0)
            }
        } else {
            navController.navigate(ScreensRouteList.RouteScreenLogin.route) {
                popUpTo(0)
            }
        }
    }
    Logo()
}
