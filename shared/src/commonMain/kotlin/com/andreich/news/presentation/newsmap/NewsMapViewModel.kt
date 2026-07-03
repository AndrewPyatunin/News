package com.andreich.news.presentation.newsmap

import com.andreich.news.domain.usecase.ObserveCitiesUseCase
import com.andreich.news.presentation.core.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update

class NewsMapViewModel(
    private val observeCitiesUseCase: ObserveCitiesUseCase
) : BaseViewModel<NewsMapState, NewsMapEvent, NewsMapIntent>(NewsMapState()) {

    override fun sendIntent(intent: NewsMapIntent) {
        launch {
            when (intent) {
                is NewsMapIntent.ClickItem -> {
                    when (intent.newsIds.size) {
                        0 -> _events.emit(NewsMapEvent.ShowError("There are no news for this city"))
                        1 -> _events.emit(NewsMapEvent.NavigateToNews(intent.newsIds[0]))
                        else -> {
                            _events.emit(NewsMapEvent.NavigateToNewsCityList(intent.newsIds))
                        }
                    }

                }

                is NewsMapIntent.StartObserving -> {
                    observeCitiesUseCase(intent.isEnglish).onStart {
                        _state.update { _state.value.copy(isLoading = true) }
                    }.onEmpty {
                        _events.emit(NewsMapEvent.ShowError("Городов не найдено!"))
                        _state.update { _state.value.copy(isLoading = false) }
                    }.onEach { list ->
                        _state.update { _state.value.copy(clusterItems = list, isLoading = false) }
                    }.collect()
                }
            }
        }

    }

    override suspend fun onError(e: Throwable) {
        _state.update { _state.value.copy(isLoading = false) }
        _events.emit(NewsMapEvent.ShowError(e.message.orEmpty()))
    }
}