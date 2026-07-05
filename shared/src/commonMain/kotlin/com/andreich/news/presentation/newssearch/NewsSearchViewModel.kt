package com.andreich.news.presentation.newssearch

import com.andreich.news.domain.model.ParamsFilter
import com.andreich.news.domain.model.RequestResult
import com.andreich.news.domain.usecase.GetSuggestionsUseCase
import com.andreich.news.domain.usecase.SaveSearchQueryUseCase
import com.andreich.news.domain.usecase.SearchNewsUseCase
import com.andreich.news.domain.usecase.UpdateSearchNewsUseCase
import com.andreich.news.presentation.core.BaseViewModel
import com.andreich.news.presentation.core.toNewsArticle
import com.andreich.news.presentation.newssearch.NewsSearchEvent.NavigateTo
import com.andreich.news.presentation.newssearch.NewsSearchEvent.ShowError
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update

class NewsSearchViewModel(
    private val searchNewsUseCase: SearchNewsUseCase,
    private val saveSearchUseCase: SaveSearchQueryUseCase,
    private val updateSearchNewsUseCase: UpdateSearchNewsUseCase,
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

    suspend fun updateSearch(query: String, paramsFilter: ParamsFilter?) {
        when (val result = updateSearchNewsUseCase(param = query, paramsFilter = paramsFilter)) {
            is RequestResult.Failure.NoInternet -> {
                sendError(result.message)
            }

            is RequestResult.Failure.Serialization -> {
                sendError(result.message)
            }

            is RequestResult.Failure.Server -> {
                sendError(result.message)
            }

            is RequestResult.Failure.Timeout -> {
                sendError(result.message)
            }

            is RequestResult.Failure.Unauthorized -> {
                sendError(result.message)
            }

            is RequestResult.Failure.Unknown -> {
                sendError(result.message)
            }

            RequestResult.Success -> {

            }
        }
    }

    private suspend fun sendError(message: String) {
        _events.emit(ShowError(message))
    }

    override fun sendIntent(intent: NewsSearchIntent) {
        launch {
            when (intent) {
                is NewsSearchIntent.SearchNews -> {
                    updateSearch(intent.param, state.value.paramsFilter)
                    searchNews(query = intent.param, paramsFilter = state.value.paramsFilter)
                    saveSearch(intent.param)
                }

                is NewsSearchIntent.ExpandedChanged -> {
                    _state.update { it.copy(expanded = intent.expanded) }
                }

                is NewsSearchIntent.QueryChanged -> {
                    _state.update { it.copy(query = intent.query) }
                }

                is NewsSearchIntent.SuggestionClicked -> {
                    searchNews(query = intent.suggestion, paramsFilter = state.value.paramsFilter)
                }

                is NewsSearchIntent.ClearQuery -> {
                    _state.update { it.copy(query = "", expanded = false) }
                }

                is NewsSearchIntent.NewsClick -> {
                    _events.emit(NavigateTo(intent.newsId))
                }

                NewsSearchIntent.FilterMenuClick -> {
                    _state.update {
                        it.copy(popUpMenuShowed = !state.value.popUpMenuShowed)
                    }
                }

                is NewsSearchIntent.SaveFilterParams -> {
                    _state.update {
                        it.copy(
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
            searchNewsUseCase(query, paramsFilter)
                .map { list -> list.map { it.toNewsArticle() } }
                .onStart {
                    _state.update { it.copy(isLoading = true) }
                }.onEach { list ->
                    _state.update {
                        it.copy(
                            resultList = list,
                            isLoading = false,
                            expanded = false
                        )
                    }
                }.onEmpty {
                    _state.update { it.copy(isLoading = false, expanded = false) }
                    _events.emit(ShowError("Ничего не найдено!"))
                }.collect()
        }
    }

    override suspend fun onError(e: Throwable) {
        _state.update { it.copy(isLoading = false) }
        _events.emit(ShowError(e.message.orEmpty()))
    }
}