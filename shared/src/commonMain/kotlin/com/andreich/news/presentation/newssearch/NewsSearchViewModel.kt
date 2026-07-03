package com.andreich.news.presentation.newssearch

import com.andreich.news.domain.model.ParamsFilter
import com.andreich.news.domain.usecase.GetSuggestionsUseCase
import com.andreich.news.domain.usecase.SaveSearchQueryUseCase
import com.andreich.news.domain.usecase.SearchNewsUseCase
import com.andreich.news.presentation.core.BaseViewModel
import com.andreich.news.presentation.newssearch.NewsSearchEvent.NavigateTo
import com.andreich.news.presentation.newssearch.NewsSearchEvent.ShowError
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update

class NewsSearchViewModel(
    private val searchNewsUseCase: SearchNewsUseCase,
    private val saveSearchUseCase: SaveSearchQueryUseCase,
    getSuggestionsUseCase: GetSuggestionsUseCase
) : BaseViewModel<NewsSearchState, NewsSearchEvent, NewsSearchIntent>(
    NewsSearchState()
) {

    private val suggestions = getSuggestionsUseCase()

    init {
        launch {
            suggestions.collect { suggestions ->
                _state.update {
                    _state.value.copy(suggestions = suggestions)
                }
            }
        }

    }

    suspend fun saveSearch(query: String) {
        saveSearchUseCase(query)
    }

    override fun sendIntent(intent: NewsSearchIntent) {
        launch {
            when (intent) {
                is NewsSearchIntent.SearchNews -> {
                    searchNews(query = intent.param, paramsFilter = state.value.paramsFilter)
                    saveSearch(intent.param)
                }

                is NewsSearchIntent.ExpandedChanged -> {
                    _state.update { _state.value.copy(expanded = intent.expanded) }
                }

                is NewsSearchIntent.QueryChanged -> {
                    _state.update { _state.value.copy(query = intent.query) }
                }

                is NewsSearchIntent.SuggestionClicked -> {
                    searchNews(query = intent.suggestion, paramsFilter = state.value.paramsFilter)
                }

                is NewsSearchIntent.ClearQuery -> {
                    _state.update { _state.value.copy(query = "", expanded = false) }
                }

                is NewsSearchIntent.NewsClick -> {
                    _events.emit(NavigateTo(intent.news))
                }

                NewsSearchIntent.FilterMenuClick -> {
                    _state.update {
                        _state.value.copy(popUpMenuShowed = !state.value.popUpMenuShowed)
                    }
                }

                is NewsSearchIntent.SaveFilterParams -> {
                    _state.update {
                        _state.value.copy(
                            paramsFilter = intent.paramsFilter,
                            popUpMenuShowed = false
                        )
                    }
                }
            }
        }
    }

    private fun searchNews(query: String, paramsFilter: ParamsFilter? = null) {
        launch {
            searchNewsUseCase(query, paramsFilter).onStart {
                _state.update { _state.value.copy(isLoading = true) }
            }.onEach { list ->
                _state.update {
                    _state.value.copy(
                        resultList = list,
                        isLoading = false,
                        expanded = false
                    )
                }
            }.onEmpty {
                _state.update { _state.value.copy(isLoading = false, expanded = false) }
                _events.emit(ShowError("Ничего не найдено!"))
            }.collect()
        }
    }

    override suspend fun onError(e: Throwable) {
        _state.update { _state.value.copy(isLoading = false) }
        _events.emit(NewsSearchEvent.ShowError(e.message.orEmpty()))
    }
}