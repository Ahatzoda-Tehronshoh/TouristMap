package com.tehronshoh.touristmap.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tehronshoh.touristmap.ui.screens.AuthorizationScreen
import com.tehronshoh.touristmap.ui.screens.SignInScreen
import com.tehronshoh.touristmap.ui.screens.SignUpScreen

@Composable
fun AppNavGraph(
    fragmentManager: FragmentManager,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = Screen.Authorization.route, modifier = modifier) {
        composable(route = Screen.Authorization.route) {
            AuthorizationScreen(
                onNavigateToLogIn = {
                    navController.navigate(Screen.LogIn.route)
                },
                onNavigateToMain = {
                    //navController.navigate(Screen.Main.route)
                },
                onNavigateToRegistration = {
                    navController.navigate(Screen.Registration.route)
                }
            )
        }

        composable(route = Screen.LogIn.route) {
            SignInScreen()
        }

        composable(route = Screen.Registration.route) {
            SignUpScreen(fragmentManager = fragmentManager)
        }
    }
}