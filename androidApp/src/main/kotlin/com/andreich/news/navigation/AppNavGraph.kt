package com.andreich.news.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    newsListContent: @Composable () -> Unit,
    newsDetailsContent: @Composable (Int) -> Unit,
    newsFavoriteContent: @Composable () -> Unit,
    newsSearchContent: @Composable () -> Unit,
    newsMapContent: @Composable () -> Unit,
    newsCityListContent: @Composable (List<Int>) -> Unit
) {
    NavHost(
        modifier = modifier,
        navController = navHostController,
        startDestination = NavDestinations.HomeGraph,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(300)
            ) + fadeIn()
        },

        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(300)
            ) + fadeOut()
        },

        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(300)
            ) + fadeIn()
        },

        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(300)
            ) + fadeOut()
        }
    ) {
        homeNavGraph(newsListContent)
        searchNavGraph(newsSearchContent)
        mapNavGraph(newsMapContent, newsCityListContent)
        favoriteNavGraph(newsFavoriteContent)
        composable<NavDestinations.NewsDetails> { backStackEntry ->
            val args = backStackEntry.toRoute<NavDestinations.NewsDetails>()
            newsDetailsContent(args.id)
        }
    }
}