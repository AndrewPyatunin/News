package com.andreich.news.presentation.newsdetail

import com.andreich.news.domain.usecase.AddToFavouritesUseCase
import com.andreich.news.domain.usecase.LoadSingleNewsUseCase
import com.andreich.news.presentation.core.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update

class NewsDetailsViewModel(
    private val addToFavouritesUseCase: AddToFavouritesUseCase,
    private val loadSingleNewsUseCase: LoadSingleNewsUseCase,
) : BaseViewModel<NewsDetailsState, NewsDetailsEvent, NewsDetailsIntent>(
    NewsDetailsState()
) {

    override fun sendIntent(intent: NewsDetailsIntent) {
        launch {
            when (intent) {
                NewsDetailsIntent.AddToFavorite -> {
                    _state.value.news?.let {
                        _events.emit(NewsDetailsEvent.AddToFavoriteSuccess("Добавлен в избранное"))
                        addToFavouritesUseCase(it)
                    }
                }

                is NewsDetailsIntent.LoadNews -> {
                    loadSingleNewsUseCase(intent.id).onStart {
                        _state.update {
                            _state.value.copy(isLoading = true)
                        }
                    }.onEach { news ->
                        _state.update {
                            _state.value.copy(news = news, isLoading = false)
                        }
                    }.collect()
                }
            }
        }
    }

    override suspend fun onError(e: Throwable) {
        _events.emit(NewsDetailsEvent.ShowError(e.message.orEmpty()))
    }
}