package com.tehronshoh.touristmap.ui.screens.home

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tehronshoh.touristmap.R
import com.tehronshoh.touristmap.extensions.sharedViewModel
import com.tehronshoh.touristmap.model.BottomNavItem
import com.tehronshoh.touristmap.model.Filter
import com.tehronshoh.touristmap.model.NetworkResult
import com.tehronshoh.touristmap.ui.tool.LocalFilteredPlaces
import com.tehronshoh.touristmap.ui.components.BottomNavigationBar
import com.tehronshoh.touristmap.ui.navigation.Screen
import com.tehronshoh.touristmap.ui.tool.LocalStaticPlaces
import com.tehronshoh.touristmap.viewmodel.MainViewModel


object HomeScreen {
    val BottomNavItems = listOf(
        BottomNavItem(
            label = Screen.Main.labelId!!,
            activeIcon = R.drawable.main_active_icon,
            inactiveIcon = R.drawable.main_inactive_icon,
            route = Screen.Main.route
        ),
        BottomNavItem(
            label = Screen.Profile.labelId!!,
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
        padding

        NavHost(
            navController = nestedNavController,
            startDestination = Screen.Main.route
        ) {
            composable(route = Screen.Main.route) {
                val viewModel = it.sharedViewModel<MainViewModel>(navController = nestedNavController)

                viewModel.getListOfPlace(Filter.DEFAULT)

                val listOfPlace = viewModel.listOfPlaceLiveData.observeAsState(initial = NetworkResult.Loading())

                CompositionLocalProvider(
                    LocalStaticPlaces provides viewModel.getListOfPlace(),
                    LocalFilteredPlaces provides listOfPlace.value
                ) {
                    MainScreen()
                }
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