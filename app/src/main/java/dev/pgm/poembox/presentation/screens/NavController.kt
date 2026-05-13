package dev.pgm.poembox.presentation.screens

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun SetUpNavController(controller: NavHostController) {
    NavHost(
        navController = controller,
        startDestination = ScreensRouteList.RouteScreenSplash.route,
        enterTransition = { fadeIn(animationSpec = tween(600)) },
        exitTransition = { fadeOut(animationSpec = tween(600)) }
    ) {
        composable(ScreensRouteList.RouteScreenSplash.route) {
            ScreenSplash(navController = controller)
        }
        composable(ScreensRouteList.RouteScreenTabs.route) {
            ScreenTabs(navController = controller)
        }
        composable(ScreensRouteList.RouteScreenLogin.route) {
            UserLogin(navController = controller)
        }
        composable(ScreensRouteList.RouteScreenCreateAccount.route) {
            CreateAccount(navController = controller)
        }
    }
}
