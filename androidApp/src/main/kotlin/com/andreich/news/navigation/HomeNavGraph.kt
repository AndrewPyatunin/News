package com.andreich.news.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute

fun NavGraphBuilder.homeNavGraph(
    newsListContent: @Composable () -> Unit,
    newsDetailsContent: @Composable (Int) -> Unit,
) {
    navigation<NavDestinations.HomeGraph>(startDestination = NavDestinations.NewsList) {
        composable<NavDestinations.NewsList> {
            newsListContent()
        }
        composable<NavDestinations.NewsDetails> { backStackEntry ->
            val args = backStackEntry.toRoute<NavDestinations.NewsDetails>()
            newsDetailsContent(args.id)
        }
    }
}