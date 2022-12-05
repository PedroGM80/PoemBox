package dev.pgm.poembox.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun SetUpNavController(controller: NavHostController) {
    NavHost(
        navController = controller,
        startDestination = ScreensRouteList.RouteScreenSplash.route
    ) {

        composable(ScreensRouteList.RouteScreenSplash.route) {
            ScreenSplash(navController = controller)
        }
        composable(ScreensRouteList.RouteScreenTabs.route) {
            ScreenTabs()
        }
        composable(ScreensRouteList.RouteScreenLogin.route) {
            UserLogin(navController = controller)

        }
        composable(ScreensRouteList.RouteScreenCreateAccount.route) {
            CreateAccount(navController = controller)

        }
    }
}

