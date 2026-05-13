package dev.pgm.poembox.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    // LaunchedEffect(Unit): corre UNA sola vez. Evita el race condition de
    // (key1 = userName) que relanzaba el efecto cuando DataStore emitía el valor
    // y causaba el delay de 1.5s doble.
    // A los 1500ms DataStore ya ha emitido (típicamente < 100ms), por lo que
    // userName.value refleja el estado real del usuario.
    LaunchedEffect(Unit) {
        delay(1500)
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
