package com.andreich.news.navigation

import com.andreich.news.R

sealed class NavigationItem(
    val destination: NavDestinations,
    val titleResId: Int,
    val iconResId: Int
) {

    object Home: NavigationItem(
        NavDestinations.HomeGraph,
        R.string.home,
        R.drawable.home_24px
    )

    object Search: NavigationItem(
        NavDestinations.SearchGraph,
        R.string.search,
        R.drawable.search_24px
    )

    object NewsMap: NavigationItem(
        NavDestinations.MapGraph,
        R.string.map,
        R.drawable.file_map_24px
    )

    object NewsFavorite: NavigationItem(
        NavDestinations.FavoriteGraph,
        R.string.favorite,
        R.drawable.favorite_24px
    )
}