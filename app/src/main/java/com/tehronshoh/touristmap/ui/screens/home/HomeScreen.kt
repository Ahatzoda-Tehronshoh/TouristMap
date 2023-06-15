package com.tehronshoh.touristmap.ui.screens.home

import android.util.Log
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tehronshoh.touristmap.R
import com.tehronshoh.touristmap.extensions.sharedViewModel
import com.tehronshoh.touristmap.model.BottomNavItem
import com.tehronshoh.touristmap.model.RouteDrawingType
import com.tehronshoh.touristmap.ui.components.BottomNavigationBar
import com.tehronshoh.touristmap.ui.navigation.Screen
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

    var bottomNavBarVisibility by remember {
        mutableStateOf(true)
    }

    var currentOpenPageInMainScreen by rememberSaveable {
        mutableStateOf(MainScreen.pages[0])
    }
    var currentOpenPageInProfileScreen by rememberSaveable {
        mutableStateOf(ProfileScreen.pages[0])
    }

    Scaffold(bottomBar = {
        if (bottomNavBarVisibility) BottomNavigationBar(navController = nestedNavController)
    }, contentColor = Color.Transparent) { padding ->
        padding

        Log.d("TAG_HOME", "HomeScreen: Home Opened!")
        NavHost(
            navController = nestedNavController, startDestination = Screen.Main.route
        ) {
            composable(route = Screen.Main.route) {
                Log.d("TAG_MAIN", "HomeScreen: Main Opened!")
                bottomNavBarVisibility = true

                MainScreen(currentOpenPage = currentOpenPageInMainScreen, openPageChanged = {
                    currentOpenPageInMainScreen = it
                }, onNavigateToPlaceDetailsScreen = { placeId ->
                    nestedNavController.navigate(
                        Screen.PlaceDetails.route + placeId.toString()
                    )
                    Log.d("TAG_HOME", "HomeScreen: $placeId")
                })

            }

            composable(route = Screen.PlaceDetails.route + "{placeId}") {
                val viewModel =
                    it.sharedViewModel<MainViewModel>(navController = nestedNavController)
                val staticListOfPlaces = viewModel.listOfPlace.value

                bottomNavBarVisibility = false

                it.arguments?.getString("placeId")?.toIntOrNull()?.let { id ->
                    Log.d("TAG_HOME", "HomeScreen: $id")
                    staticListOfPlaces.firstOrNull { place -> (id == place.id) }?.let { place ->
                        Log.d("TAG_HOME", "HomeScreen: $place")

                        PlaceDetailsScreen(
                            place = place,
                            showOnMap = { showingPlace, routeSettings ->
                                currentOpenPageInMainScreen = MainScreen.pages[1].also { map ->
                                    map.place = showingPlace

                                    if (routeSettings != null) {
                                        if (routeSettings.drawingType != RouteDrawingType.CLEAR && routeSettings.routeType == map.routeSettings?.routeType && routeSettings.transportType == map.routeSettings?.transportType)
                                            map.routeSettings = map.routeSettings?.also { mapRS ->
                                                mapRS.points =
                                                    mapRS.points.toMutableList().also { list ->
                                                        list.add(routeSettings.points[0])
                                                    }.toList()
                                            } ?: routeSettings
                                        else
                                            map.routeSettings = routeSettings
                                    }

                                }

                                nestedNavController.navigate(Screen.Main.route) {
                                    popUpTo(Screen.Main.route)
                                    launchSingleTop = true
                                }
                            }) {
                            nestedNavController.navigateUp()
                        }
                    }
                }
            }


            composable(route = Screen.Profile.route) {
                bottomNavBarVisibility = true

                ProfileScreen(profileOpenPage = currentOpenPageInProfileScreen, onPageChange = {
                    currentOpenPageInProfileScreen = it
                }) { placeId ->
                    nestedNavController.navigate(
                        Screen.PlaceDetails.route + placeId.toString()
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen()
}