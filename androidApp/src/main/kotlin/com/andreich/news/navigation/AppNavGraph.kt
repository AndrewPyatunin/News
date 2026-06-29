package com.andreich.news.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    newsListContent: @Composable () -> Unit,
    newsDetailsContent: @Composable (Int) -> Unit,
    newsFavoriteContent: @Composable () -> Unit,
    newsSearchContent: @Composable () -> Unit,
    newsMapContent: @Composable () -> Unit,
) {
    NavHost(
        modifier = modifier,
        navController = navHostController,
        startDestination = NavDestinations.HomeGraph
    ) {
        homeNavGraph(newsListContent, newsDetailsContent)
        searchNavGraph(newsDetailsContent, newsSearchContent)
        mapNavGraph(newsDetailsContent, newsMapContent)
        favoriteNavGraph(newsDetailsContent, newsFavoriteContent)
    }
}