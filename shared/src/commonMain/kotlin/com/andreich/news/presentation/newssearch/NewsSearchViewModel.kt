package com.andreich.news.presentation.newssearch

import com.andreich.news.domain.usecase.GetSuggestionsUseCase
import com.andreich.news.domain.usecase.SaveSearchQueryUseCase
import com.andreich.news.domain.usecase.SearchNewsUseCase
import com.andreich.news.presentation.core.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
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
                    searchNews(intent.param)
                    saveSearch(intent.param)
                }

                is NewsSearchIntent.ExpandedChanged -> {
                    _state.update { _state.value.copy(expanded = intent.expanded) }
                }
                is NewsSearchIntent.QueryChanged -> {
                    _state.update { _state.value.copy(query = intent.query) }
                }
                is NewsSearchIntent.SuggestionClicked -> {
                    searchNews(intent.suggestion)
                }

                is NewsSearchIntent.ClearQuery -> {
                    _state.update { _state.value.copy(query = "", expanded = false, ) }
                }

                is NewsSearchIntent.NewsClick -> {
                    _events.emit(NewsSearchEvent.NavigateTo(intent.news))
                }
            }
        }
    }

    private fun searchNews(query: String) {
        launch {
            searchNewsUseCase(query).onStart {
                _state.update { _state.value.copy(isLoading = true) }
            }.onEach { list ->
                _state.update { _state.value.copy(resultList = list) }
            }.collect()
        }
    }

    override suspend fun onError(e: Throwable) {
        _events.emit(NewsSearchEvent.ShowError(e.message.orEmpty()))
    }
}