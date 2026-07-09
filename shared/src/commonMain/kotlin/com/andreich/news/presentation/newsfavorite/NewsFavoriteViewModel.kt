package com.andreich.news.presentation.newsfavorite

import com.andreich.news.domain.usecase.AddToFavouritesUseCase
import com.andreich.news.domain.usecase.LoadFavoritesNewsUseCase
import com.andreich.news.domain.usecase.RemoveFromFavouritesUseCase
import com.andreich.news.presentation.core.BaseViewModel
import com.andreich.news.presentation.core.UiMessage
import com.andreich.news.presentation.core.toNewsArticle
import com.andreich.news.presentation.newsfavorite.NewsFavoriteEvent.NavigateToNewsDetail
import com.andreich.news.presentation.newsfavorite.NewsFavoriteEvent.ShowUndoRemove
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update

class NewsFavoriteViewModel(
    private val removeFromFavouritesUseCase: RemoveFromFavouritesUseCase,
    private val loadFavoritesNewsUseCase: LoadFavoritesNewsUseCase,
    private val addToFavouritesUseCase: AddToFavouritesUseCase
) : BaseViewModel<NewsFavoriteState, NewsFavoriteEvent, NewsFavoriteIntent>(
    NewsFavoriteState()
) {
    override fun sendIntent(intent: NewsFavoriteIntent) {
        launch {
            when (intent) {
                is NewsFavoriteIntent.ClickNews -> {
                    _events.emit(NavigateToNewsDetail(intent.newsId))
                }

                is NewsFavoriteIntent.RemoveNews -> {
                    removeFromFavouritesUseCase(intent.newsId)
                    _events.emit(ShowUndoRemove(intent.newsId))

                }

                is NewsFavoriteIntent.LoadFavourites -> {
                    loadFavoritesNewsUseCase().map { list ->
                        list.map { it.toNewsArticle() }
                    }.onStart {
                        _state.update {
                            _state.value.copy(isLoading = true)
                        }
                    }.onEmpty {
                        _state.update { it.copy(isLoading = false) }
                        _messages.emit(UiMessage.ShowError("Новостей нет!"))
                    }.onEach { list ->
                        _state.update {
                            _state.value.copy(news = list, isLoading = false)
                        }
                    }.collect()
                }

                is NewsFavoriteIntent.UndoRemove -> {
                    addToFavouritesUseCase(intent.newsId)
                }
            }
        }
    }

    override suspend fun onError(e: Throwable) {
        _state.update { _state.value.copy(isLoading = false) }
        _messages.emit(UiMessage.ShowError(e.message.orEmpty()))
    }

}