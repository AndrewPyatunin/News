package com.andreich.news.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation

fun NavGraphBuilder.homeNavGraph(
    newsListContent: @Composable () -> Unit
) {
    navigation<NavDestinations.HomeGraph>(startDestination = NavDestinations.NewsList) {
        composable<NavDestinations.NewsList> {
            newsListContent()
        }
    }
}