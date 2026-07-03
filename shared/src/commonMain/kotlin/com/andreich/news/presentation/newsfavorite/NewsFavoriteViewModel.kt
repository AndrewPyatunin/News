package com.andreich.news.presentation.newsfavorite

import com.andreich.news.domain.usecase.AddToFavouritesUseCase
import com.andreich.news.domain.usecase.LoadFavoritesNewsUseCase
import com.andreich.news.domain.usecase.RemoveFromFavouritesUseCase
import com.andreich.news.presentation.core.BaseViewModel
import com.andreich.news.presentation.newsfavorite.NewsFavoriteEvent.NavigateToNewsDetail
import com.andreich.news.presentation.newsfavorite.NewsFavoriteEvent.ShowUndoRemove
import kotlinx.coroutines.flow.collect
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
                    _events.emit(NavigateToNewsDetail(intent.news))
                }

                is NewsFavoriteIntent.RemoveNews -> {
                    removeFromFavouritesUseCase(intent.news.id)
                    _events.emit(ShowUndoRemove(intent.news))

                }

                is NewsFavoriteIntent.LoadFavourites -> {
                    loadFavoritesNewsUseCase().onStart {
                        _state.update {
                            _state.value.copy(isLoading = true)
                        }
                    }.onEmpty {
                        _state.update { it.copy(isLoading = false) }
                        _events.emit(NewsFavoriteEvent.ShowError("Новостей нет!"))
                    }.onEach { list ->
                        _state.update {
                            _state.value.copy(news = list, isLoading = false)
                        }
                    }.collect()
                }

                is NewsFavoriteIntent.UndoRemove -> {
                    addToFavouritesUseCase(intent.news.copy())
                }
            }
        }
    }

    override suspend fun onError(e: Throwable) {
        _state.update { _state.value.copy(isLoading = false) }
        _events.emit(NewsFavoriteEvent.ShowError(e.message.orEmpty()))
    }

}