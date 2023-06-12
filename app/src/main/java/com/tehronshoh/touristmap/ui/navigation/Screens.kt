package com.tehronshoh.touristmap.ui.navigation

import androidx.annotation.StringRes
import com.tehronshoh.touristmap.R

sealed class Screen(val route: String, @StringRes val labelId: Int? = null) {
    object SplashScreen: Screen(SPLASH_SCREEN_ROUTE)
    object LogIn: Screen(LOG_IN_ROUTE)
    object Registration: Screen(REGISTRATION_ROUTE)
    object Home: Screen(HOME_ROUTE)
    object Main: Screen(route = MAIN_ROUTE, labelId = R.string.main)
    object Search: Screen(route = SEARCH_ROUTE, labelId = R.string.search)
    object Map: Screen(route = MAP_ROUTE, labelId = R.string.map)
    object Profile: Screen(route = PROFILE_ROUTE, labelId = R.string.profile)
}
