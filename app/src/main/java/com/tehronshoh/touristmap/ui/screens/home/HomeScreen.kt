package com.tehronshoh.touristmap.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tehronshoh.touristmap.R
import com.tehronshoh.touristmap.model.BottomNavItem
import com.tehronshoh.touristmap.ui.components.BottomNavigationBar
import com.tehronshoh.touristmap.ui.navigation.Screen


object HomeScreen {
    val BottomNavItems = listOf(
        BottomNavItem(
            label = "Главная",
            activeIcon = R.drawable.main_active_icon,
            inactiveIcon = R.drawable.main_inactive_icon,
            route = Screen.Main.route
        ),
        BottomNavItem(
            label = "Профиль",
            activeIcon = R.drawable.profile_active_icon,
            inactiveIcon = R.drawable.profile_inactive_icon,
            route = Screen.Profile.route
        )
    )
}

@Composable
fun HomeScreen() {
    val nestedNavController = rememberNavController()

    Scaffold(bottomBar = {
        BottomNavigationBar(navController = nestedNavController)
    }, contentColor = Color.Transparent) { padding ->
        val scaffoldPadding = padding

        NavHost(
            navController = nestedNavController,
            startDestination = Screen.Main.route
        ) {
            composable(route = Screen.Main.route) {
                MainScreen()
            }

            composable(route = Screen.Profile.route) {
                ProfileScreen()
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen()
}