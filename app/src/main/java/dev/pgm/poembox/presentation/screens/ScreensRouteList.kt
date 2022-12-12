package dev.pgm.poembox.presentation.screens

/**
 * Screens route list
 *
 * @constructor Create empty Screens route list
 * @property route
 */
sealed class ScreensRouteList(val route: String) {
    object RouteScreenSplash : ScreensRouteList("S")
    object RouteScreenTabs : ScreensRouteList("T")
    object RouteScreenLogin : ScreensRouteList("L")
    object RouteScreenCreateAccount : ScreensRouteList("C")
}