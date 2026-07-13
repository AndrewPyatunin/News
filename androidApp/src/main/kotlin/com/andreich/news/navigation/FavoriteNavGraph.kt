package com.andreich.news.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation

fun NavGraphBuilder.favoriteNavGraph(
    newsFavoriteContent: @Composable () -> Unit,
) {
    navigation<NavDestinations.FavoriteGraph>(startDestination = NavDestinations.NewsFavorite) {
        composable<NavDestinations.NewsFavorite> {
            newsFavoriteContent()
        }
    }

}