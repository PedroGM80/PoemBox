package dev.pgm.poembox.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.pgm.poembox.presentation.components.Logo
import dev.pgm.poembox.presentation.viewmodels.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

private const val SPLASH_DELAY_MS = 1500L

@Composable
fun ScreenSplash(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        delay(SPLASH_DELAY_MS)
        // Wait until DataStore has emitted its first value
        viewModel.isLoaded.first { it }

        val destination = when {
            !viewModel.onboardingCompleted.value ->
                ScreensRouteList.RouteScreenOnboarding.route
            else ->
                ScreensRouteList.RouteScreenTabs.route
        }
        navController.navigate(destination) { popUpTo(0) }
    }
    Logo()
}
