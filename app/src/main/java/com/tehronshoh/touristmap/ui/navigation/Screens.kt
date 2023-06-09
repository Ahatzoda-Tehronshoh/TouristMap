package com.tehronshoh.touristmap.ui.navigation

sealed class Screen(val route: String) {
    object SplashScreen: Screen(SPLASH_SCREEN_ROUTE)
    object Authorization: Screen(AUTHORIZATION_ROUTE)
    object LogIn: Screen(LOG_IN_ROUTE)
    object Registration: Screen(REGISTRATION_ROUTE)
    object Main: Screen(MAIN_ROUTE)
}
