package com.andreich.news.presentation.newscitylist

import com.andreich.news.domain.usecase.LoadNewsByIdsUseCase
import com.andreich.news.presentation.core.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import org.jetbrains.compose.resources.stringResource

class NewsCityListViewModel(
    private val loadNewsByIdsUseCase: LoadNewsByIdsUseCase
) : BaseViewModel<NewsCityListState, NewsCityListEvent, NewsCityListIntent>(NewsCityListState()) {

    override fun sendIntent(intent: NewsCityListIntent) {
        launch {
            when (intent) {
                is NewsCityListIntent.LoadNewsList -> {
                    loadNewsByIdsUseCase(intent.ids)
                        .onStart {
                            _state.update {
                                _state.value.copy(isLoading = true)
                            }
                        }.onEmpty {
                            _state.update { it.copy(isLoading = false) }
                            _events.emit(NewsCityListEvent.ShowError(message = "Новостей не найдено!"))
                        }.onEach { list ->
                            _state.update {
                                _state.value.copy(newsList = list, isLoading = false)
                            }
                        }.collect()
                }

                is NewsCityListIntent.NewsClick -> {
                    _events.emit(NewsCityListEvent.NavigateToDetails(intent.newsId))
                }
            }
        }

    }

    override suspend fun onError(e: Throwable) {
        _state.update { _state.value.copy(isLoading = false) }
        _events.emit(NewsCityListEvent.ShowError(e.message.orEmpty()))
    }
}