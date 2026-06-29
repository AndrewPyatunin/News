package com.andreich.news.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute

fun NavGraphBuilder.favoriteNavGraph(
    newsDetailsContent: @Composable (Int) -> Unit,
    newsFavoriteContent: @Composable () -> Unit,
) {
    navigation<NavDestinations.FavoriteGraph>(startDestination = NavDestinations.NewsFavorite) {
        composable<NavDestinations.NewsFavorite> {
            newsFavoriteContent()
        }
        composable<NavDestinations.NewsDetails> { backStackEntry ->
            val args = backStackEntry.toRoute<NavDestinations.NewsDetails>()
            newsDetailsContent(args.id)
        }
    }

}