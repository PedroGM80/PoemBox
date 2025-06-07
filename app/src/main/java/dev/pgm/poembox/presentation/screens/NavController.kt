package dev.pgm.poembox.presentation.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

/**
 * Set up nav controller
 *
 * @param controller
 * @param userData
 */
@OptIn(ExperimentalAnimationApi::class)
@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun SetUpNavController(controller: NavHostController, userData: String) {
    AnimatedNavHost(
        navController = controller,
        startDestination = ScreensRouteList.RouteScreenSplash.route,
        enterTransition = { fadeIn(animationSpec = tween(700)) },
        exitTransition = { fadeOut(animationSpec = tween(700)) }
    ) {

        composable(ScreensRouteList.RouteScreenSplash.route) {
            ScreenSplash(navController = controller, userData)
        }
        composable(ScreensRouteList.RouteScreenTabs.route) {
            ScreenTabs(userData)
        }
        composable(ScreensRouteList.RouteScreenLogin.route) {
            UserLogin(navController = controller, userData)

        }
        composable(ScreensRouteList.RouteScreenCreateAccount.route) {
            CreateAccount(navController = controller)

        }
    }
}

