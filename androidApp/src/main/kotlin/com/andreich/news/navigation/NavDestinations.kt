package com.andreich.news.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface NavDestinations {

    @Serializable
    data object NewsList : NavDestinations

    @Serializable
    data class NewsDetails(val id: Int) : NavDestinations

    @Serializable
    data object NewsFavorite : NavDestinations

    @Serializable
    data object NewsSearch : NavDestinations

    @Serializable
    data object NewsMap : NavDestinations

    @Serializable
    data class NewsCityList(val newsIds: List<Int>) : NavDestinations

    @Serializable
    data object HomeGraph : NavDestinations

    @Serializable
    data object FavoriteGraph : NavDestinations

    @Serializable
    data object MapGraph : NavDestinations

    @Serializable
    data object SearchGraph : NavDestinations
}

