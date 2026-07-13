package com.andreich.news.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute

fun NavGraphBuilder.mapNavGraph(
    newsMapContent: @Composable () -> Unit,
    newsCityListContent: @Composable (List<Int>) -> Unit
) {
    navigation<NavDestinations.MapGraph>(startDestination = NavDestinations.NewsMap) {
        composable<NavDestinations.NewsMap> {
            newsMapContent()
        }
        composable<NavDestinations.NewsCityList> { backStackEntry ->
            val args = backStackEntry.toRoute<NavDestinations.NewsCityList>()
            newsCityListContent(args.newsIds)
        }
    }
}