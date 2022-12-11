package dev.pgm.poembox.presentation.screens

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import dev.pgm.poembox.presentation.components.Logo
import kotlinx.coroutines.delay

@Composable
fun ScreenSplash(navController: NavController, userData: String) {

    LaunchedEffect(key1 = true) {
        delay(1500)
        Log.i(":::USER_DATA", userData.length.toString())
        if (userData.isEmpty()) {
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

/*
@Preview(showBackground = true)
@Composable
private fun ShowSplash() {
    ScreenSplash(rememberNavController(), userData)
}
*/
