package dev.pgm.poembox.presentation.screens

sealed class ScreensRouteList(val route: String) {
    object RouteScreenSplash : ScreensRouteList("S")
    object RouteScreenTabs : ScreensRouteList("T")
    object RouteScreenLogin : ScreensRouteList("L")
    object RouteScreenCreateAccount : ScreensRouteList("C")
}