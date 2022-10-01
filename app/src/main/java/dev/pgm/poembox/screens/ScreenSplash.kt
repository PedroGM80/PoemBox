package dev.pgm.poembox.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.pgm.poembox.components.Logo
import kotlinx.coroutines.delay

@Composable
fun ScreenSplash (navController: NavController){
    LaunchedEffect(key1=true){
        delay(1500)
        navController.navigate(ScreensRouteList.RouteScreenTabs.route){
            popUpTo(0)
        }
    }
 Logo()
}
@Preview(showBackground = true)
@Composable
private fun ShowSplash(){
   ScreenSplash(rememberNavController( ))
}
