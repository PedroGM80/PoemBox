package dev.pgm.poembox.presentation.screens

/**
 * Screens route list
 *
 * @constructor Create empty Screens route list
 * @property route
 */
sealed class ScreensRouteList(val route: String) {
    object RouteScreenSplash : ScreensRouteList("splash")
    object RouteScreenTabs : ScreensRouteList("tabs")
    object RouteScreenLogin : ScreensRouteList("login")
    object RouteScreenCreateAccount : ScreensRouteList("create_account")
}