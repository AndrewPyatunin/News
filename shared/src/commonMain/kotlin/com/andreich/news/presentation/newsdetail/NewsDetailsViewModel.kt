package com.andreich.news.presentation.newsdetail

import com.andreich.news.domain.usecase.AddToFavouritesUseCase
import com.andreich.news.domain.usecase.LoadFavoritesNewsUseCase
import com.andreich.news.domain.usecase.LoadSingleNewsUseCase
import com.andreich.news.domain.usecase.RemoveFromFavouritesUseCase
import com.andreich.news.presentation.core.BaseViewModel
import com.andreich.news.presentation.core.UiMessage
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
                        addToFavouritesUseCase(it.id).run {
                            _messages.emit(UiMessage.ShowSuccess(intent.message))
                        }
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
                        _messages.emit(UiMessage.ShowError("Что-то пошло не так!"))
                    }.collect { (news, favorites) ->
                        val favoriteIds = favorites.map { it.id }
                        _state.update {
                            _state.value.copy(
                                news = news,
                                isLoading = false,
                                isFavorite = news.id in favoriteIds,
                                chunks = news.content.prepareNewsContent()
                            )
                        }
                    }
                }

                is NewsDetailsIntent.RemoveFromFavorite -> {
                    _messages.emit(UiMessage.ShowSuccess(intent.message))
                    _state.value.news?.let {
                        removeFromFavouritesUseCase(it.id)
                    }
                }
            }
        }
    }

    override suspend fun onError(e: Throwable) {
        _state.update { _state.value.copy(isLoading = false) }
        _messages.emit(UiMessage.ShowSuccess(e.message.toString()))
    }

    private fun String.prepareNewsContent(chunkSize: Int = 3000): List<String> {
            val result = mutableListOf<String>()
            val builder = StringBuilder()

            lines().forEach { paragraph ->

                if (
                    builder.isNotEmpty() &&
                    builder.length + paragraph.length > chunkSize
                ) {
                    result += builder.toString()
                    builder.clear()
                }

                if (builder.isNotEmpty()) {
                    builder.append("\n")
                }

                builder.append(paragraph)
            }

            if (builder.isNotEmpty()) {
                result += builder.toString()
            }

            return result.toList()
        }
}