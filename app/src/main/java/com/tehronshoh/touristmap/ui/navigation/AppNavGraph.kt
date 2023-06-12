package com.tehronshoh.touristmap.ui.navigation

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tehronshoh.touristmap.extensions.sharedViewModel
import com.tehronshoh.touristmap.model.NetworkResult
import com.tehronshoh.touristmap.model.User
import com.tehronshoh.touristmap.ui.screens.HomeScreen
import com.tehronshoh.touristmap.ui.screens.SignInScreen
import com.tehronshoh.touristmap.ui.screens.SignUpScreen
import com.tehronshoh.touristmap.ui.screens.SplashScreen
import com.tehronshoh.touristmap.viewmodel.SignInViewModel
import com.tehronshoh.touristmap.viewmodel.SignUpViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(
    fragmentManager: FragmentManager,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = Screen.SplashScreen.route,
        modifier = modifier
    ) {
        composable(route = Screen.SplashScreen.route) {
            SplashScreen {
                navController.navigate(Screen.LogIn.route) {
                    popUpTo(route = Screen.SplashScreen.route) {
                        inclusive = true
                    }
                }
            }
        }

        composable(route = Screen.LogIn.route) {
            val signInViewModel = it.sharedViewModel<SignInViewModel>(navController = navController)
            val coroutineScope = rememberCoroutineScope()

            var isLoading by remember { mutableStateOf(false) }

            SignInScreen(
                isLoading = isLoading,
                onNavigateToRegister = {
                    navController.navigate(Screen.Registration.route)
                },
                onNavigateToMainWithoutLogIn = {
                    navController.navigate(Screen.Home.route)
                }
            ) { user ->
                coroutineScope.launch(Dispatchers.Main) {
                    signInViewModel.signIn(user).collect { result ->
                        when (result) {
                            is NetworkResult.Loading<List<User>> -> {
                                isLoading = true
                            }

                            is NetworkResult.Success<List<User>> -> {
                                isLoading = false
                                if (result.data!!.size == 1) {
                                    navController.navigate(Screen.Home.route)
                                } else {
                                    Log.d("TAG_AUTH", "AppNavGraph: Wrong!")
                                    Toast.makeText(
                                        context,
                                        "Password or Email is wrong! try Again!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            is NetworkResult.Error<List<User>> -> {
                                isLoading = false
                                Log.d("TAG_AUTH", "AppNavGraph: ${result.message}")
                                Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }

        composable(route = Screen.Registration.route) {
            val viewModel = it.sharedViewModel<SignUpViewModel>(navController = navController)
            val coroutineScope = rememberCoroutineScope()

            var isLoading by remember { mutableStateOf(false) }

            SignUpScreen(
                fragmentManager = fragmentManager,
                isLoading = isLoading,
                onNavigateToLogIn = {
                    navController.navigateUp()
                },
                onNavigateToMainWithoutLogIn = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(route = Screen.LogIn.route)
                    }
                }
            ) { user ->
                coroutineScope.launch(Dispatchers.Main) {
                    viewModel.registration(user).collect { result ->
                        when (result) {
                            is NetworkResult.Loading<String> -> {
                                isLoading = true
                            }

                            is NetworkResult.Success<String> -> {
                                isLoading = false
                                navController.navigate(Screen.Home.route)
                            }

                            is NetworkResult.Error<String> -> {
                                isLoading = false
                                Log.d("TAG_AUTH", "AppNavGraph: ${result.message}")
                                Toast.makeText(context, result.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }
            }
        }

        composable(route = Screen.Home.route) {
            HomeScreen()
        }
    }
}