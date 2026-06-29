package com.andreich.news.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.andreich.news.R
import com.andreich.news.navigation.AppNavGraph
import com.andreich.news.navigation.NavDestinations
import com.andreich.news.navigation.NavigationItem
import com.andreich.news.navigation.rememberNavigationState

@Composable
fun MainScreen() {
    val navigationState = rememberNavigationState()
    val snackBarState = remember { SnackbarHostState() }
    Scaffold(
        modifier = Modifier,
        snackbarHost = {
            SnackbarHost(snackBarState)
        },
        topBar = {
            TopAppBar(title = {
                Text(
                    navigationState.navHostController.currentBackStackEntry?.destination?.route
                        ?: "News"
                )
            }, navigationIcon = {
                IconButton(onClick = {
                    navigationState.navHostController.popBackStack()
                }) {
                    Icon(
                        painterResource(R.drawable.arrow_back_24px),
                        contentDescription = null
                    )
                }
            }, actions = {
                IconButton(onClick = {}) {
                    Icon(
                        painterResource(R.drawable.menu_24px),
                        contentDescription = null
                    )
                }
            })
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navigationState.navHostController.currentBackStackEntryAsState()
                val navItems =
                    listOf(NavigationItem.Home, NavigationItem.Search, NavigationItem.NewsMap,
                        NavigationItem.NewsFavorite)
                navItems.forEachIndexed { index, item ->
                    val selected = navBackStackEntry?.destination?.hierarchy?.any {
                        it == item.destination
                    } ?: false
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(item.iconResId),
                                contentDescription = stringResource(item.titleResId)
                            )
                        },
                        label = {
                            Text(stringResource(item.titleResId))
                        },
                        selected = selected,
                        onClick = {
                            navigationState.navigateTo(item.destination)
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        AppNavGraph(
            modifier = Modifier.padding(paddingValues),
            navHostController = navigationState.navHostController,
            newsListContent = {
                NewsListRoute(snackBarState = snackBarState, onNavigateToNewsDetails = {
                    navigationState.navHostController.navigate(NavDestinations.NewsDetails(it.id))
                })
            }, newsDetailsContent = {
                NewsDetailsRoute(
                    snackBarState = snackBarState,
                    newsId = it
                )
            }, newsFavoriteContent = {
                NewsFavoriteRoute(snackbarHostState = snackBarState, onNavigateToNewsDetails = {
                    navigationState.navHostController.navigate(NavDestinations.NewsDetails(it))
                })
            }, newsSearchContent = {
                NewsSearchRoute(snackbarState = snackBarState, onNavigateToNewsDetails = {
                    navigationState.navHostController.navigate(NavDestinations.NewsDetails(it.id))
                })
            }, newsMapContent = {
                NewsMapRoute()
            }
        )
    }
}