package dev.pgm.poembox.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun SetUpNavController(controller: NavHostController) {
    NavHost(navController = controller, startDestination = ScreensRouteList.RouteScreenSplash.route) {

        composable(ScreensRouteList.RouteScreenSplash.route) {
            ScreenSplash(navController = controller)
        }
        composable(ScreensRouteList.RouteScreenTabs.route) {
            ScreenTabs()
        }
    }
}

