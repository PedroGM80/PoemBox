package dev.pgm.poembox.screens

sealed class ScreensRouteList(val route:String){
    object RouteScreenSplash: ScreensRouteList("S")
    object RouteScreenTabs: ScreensRouteList("T")
   object RouteScreenLogin: ScreensRouteList("L")
}