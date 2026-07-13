package com.andreich.news.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation

fun NavGraphBuilder.searchNavGraph(
    newsSearchContent: @Composable () -> Unit
) {
    navigation<NavDestinations.SearchGraph>(startDestination = NavDestinations.NewsSearch) {
        composable<NavDestinations.NewsSearch> { newsSearchContent() }
    }
}