package com.tehronshoh.touristmap.ui.navigation

import androidx.annotation.StringRes
import com.tehronshoh.touristmap.R

sealed class Screen(val route: String, @StringRes val resourceId: Int? = null) {
    object SplashScreen: Screen(SPLASH_SCREEN_ROUTE)
    object Authorization: Screen(AUTHORIZATION_ROUTE)
    object LogIn: Screen(LOG_IN_ROUTE)
    object Registration: Screen(REGISTRATION_ROUTE)
    object Home: Screen(HOME_ROUTE)
    object Main: Screen(route = MAIN_ROUTE, resourceId = R.string.main)
    object Profile: Screen(route = PROFILE_ROUTE, resourceId = R.string.profile)
}
