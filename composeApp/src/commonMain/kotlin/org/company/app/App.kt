package org.company.app

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.company.app.screen.LoginScreen
import org.company.app.screen.MainScreen
import org.company.app.theme.AppTheme
import org.company.app.utils.changeWindowSize

enum class AppNavigation(val route: String) {
    Main("main"), Login("login")
}

@Composable
internal fun App() = AppTheme {
    val navigator = rememberNavController()

    NavHost(
        startDestination = AppNavigation.Login.route,
        navController = navigator,
        modifier = Modifier.fillMaxSize(),
    ) {
        composable(AppNavigation.Login.route) {
            LaunchedEffect(Unit) {
                changeWindowSize(800, 600)
            }

            LoginScreen(navigateToMain = {
                navigator.navigate(AppNavigation.Main.route) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            })
        }

        composable(AppNavigation.Main.route) {
            LaunchedEffect(Unit) {
                changeWindowSize(1400, 1050)
            }

            MainScreen(navigateToLogin = {
                navigator.navigate(AppNavigation.Login.route) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            })
        }
    }

}