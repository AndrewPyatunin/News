package com.andreich.news.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute

fun NavGraphBuilder.mapNavGraph(
    newsDetailsContent: @Composable (Int) -> Unit,
    newsMapContent: @Composable () -> Unit
) {
    navigation<NavDestinations.MapGraph>(startDestination = NavDestinations.NewsMap) {
        composable<NavDestinations.NewsMap> {
            newsMapContent()
        }
        composable<NavDestinations.NewsDetails> { backStackEntry ->
            val args = backStackEntry.toRoute<NavDestinations.NewsDetails>()
            newsDetailsContent(args.id)
        }
    }
}