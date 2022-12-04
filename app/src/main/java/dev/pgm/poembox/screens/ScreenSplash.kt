package dev.pgm.poembox.screens

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.pgm.poembox.MainActivity.Companion.USER_DATA
import dev.pgm.poembox.components.Logo
import kotlinx.coroutines.delay

@Composable
fun ScreenSplash(navController: NavController) {

    LaunchedEffect(key1 = true) {
        delay(1500)
        val loadedUserData = USER_DATA
        Log.i(":::USER_DATA", loadedUserData.length.toString())
        if (loadedUserData.isEmpty()) {
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

@Preview(showBackground = true)
@Composable
private fun ShowSplash() {
    ScreenSplash(rememberNavController())
}
