package com.andreich.news.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class NavigationState(
    val navHostController: NavHostController
) {

    fun navigateTo(destination: NavDestinations) {
        navHostController.popGlobalDestinations()

        navHostController.navigate(destination) {
            popUpTo(navHostController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    private fun NavHostController.popGlobalDestinations() {
        while (currentBackStackEntry?.destination?.hasRoute<NavDestinations.NewsDetails>() == true) {
            popBackStack()
        }
    }

}

@Composable
fun rememberNavigationState(navController: NavHostController = rememberNavController()) =
    remember { NavigationState(navController) }