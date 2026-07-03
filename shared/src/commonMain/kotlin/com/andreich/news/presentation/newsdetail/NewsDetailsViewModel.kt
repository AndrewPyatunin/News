package com.andreich.news.presentation.newsdetail

import com.andreich.news.domain.usecase.AddToFavouritesUseCase
import com.andreich.news.domain.usecase.LoadFavoritesNewsUseCase
import com.andreich.news.domain.usecase.LoadSingleNewsUseCase
import com.andreich.news.domain.usecase.RemoveFromFavouritesUseCase
import com.andreich.news.presentation.core.BaseViewModel
import com.andreich.news.presentation.newsdetail.NewsDetailsEvent.AddToFavoriteSuccess
import com.andreich.news.presentation.newsdetail.NewsDetailsEvent.RemoveFromFavoriteSuccess
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update

class NewsDetailsViewModel(
    private val addToFavouritesUseCase: AddToFavouritesUseCase,
    private val removeFromFavouritesUseCase: RemoveFromFavouritesUseCase,
    private val loadSingleNewsUseCase: LoadSingleNewsUseCase,
    private val loadFavoritesNewsUseCase: LoadFavoritesNewsUseCase
) : BaseViewModel<NewsDetailsState, NewsDetailsEvent, NewsDetailsIntent>(
    NewsDetailsState()
) {

    override fun sendIntent(intent: NewsDetailsIntent) {
        launch {
            when (intent) {
                is NewsDetailsIntent.AddToFavorite -> {
                    _state.value.news?.let {
                        _events.emit(AddToFavoriteSuccess(intent.message))
                        addToFavouritesUseCase(it)
                    }
                }

                is NewsDetailsIntent.LoadNews -> {
                    combine(
                        loadSingleNewsUseCase(intent.id),
                        loadFavoritesNewsUseCase()
                    ) { news, favorites ->
                        news to favorites
                    }.onStart {
                        _state.update {
                            _state.value.copy(isLoading = true)
                        }
                    }.onEmpty {
                        _state.update { it.copy(isLoading = false) }
                        _events.emit(NewsDetailsEvent.ShowError("Что-то пошло не так!"))
                    }.collect { (news, favorites) ->
                        val favoriteIds = favorites.map { it.id }
                        _state.update {
                            _state.value.copy(
                                news = news,
                                isLoading = false,
                                isFavorite = news.id in favoriteIds
                            )
                        }
                    }
                }

                is NewsDetailsIntent.RemoveFromFavorite -> {
                    _state.value.news?.let {
                        _events.emit(RemoveFromFavoriteSuccess(intent.message))
                        removeFromFavouritesUseCase(it.id)
                    }
                }
            }
        }
    }

    override suspend fun onError(e: Throwable) {
        _state.update { _state.value.copy(isLoading = false) }
        _events.emit(NewsDetailsEvent.ShowError(e.message.orEmpty()))
    }
}