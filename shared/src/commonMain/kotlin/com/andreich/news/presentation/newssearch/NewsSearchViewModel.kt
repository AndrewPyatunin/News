package com.andreich.news.presentation.newssearch

import com.andreich.news.domain.model.Country
import com.andreich.news.domain.model.Language
import com.andreich.news.domain.model.ParamsFilter
import com.andreich.news.domain.model.RequestResult
import com.andreich.news.domain.model.UserSettings
import com.andreich.news.domain.usecase.GetNewsSuggestionsUseCase
import com.andreich.news.domain.usecase.GetSuggestionsUseCase
import com.andreich.news.domain.usecase.GetUserSettingsUseCase
import com.andreich.news.domain.usecase.SaveSearchQueryUseCase
import com.andreich.news.domain.usecase.SearchNewsUseCase
import com.andreich.news.domain.usecase.UpdateSearchNewsUseCase
import com.andreich.news.domain.usecase.UpdateUserSettingsUseCase
import com.andreich.news.presentation.core.BaseViewModel
import com.andreich.news.presentation.core.toNewsArticle
import com.andreich.news.presentation.newssearch.NewsSearchEvent.NavigateTo
import com.andreich.news.presentation.newssearch.NewsSearchEvent.ShowError
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update

class NewsSearchViewModel(
    private val updateUserSettingsUseCase: UpdateUserSettingsUseCase,
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
    private val searchNewsUseCase: SearchNewsUseCase,
    private val saveSearchUseCase: SaveSearchQueryUseCase,
    private val updateSearchNewsUseCase: UpdateSearchNewsUseCase,
    private val getNewsSuggestionsUseCase: GetNewsSuggestionsUseCase,
    getSuggestionsUseCase: GetSuggestionsUseCase
) : BaseViewModel<NewsSearchState, NewsSearchEvent, NewsSearchIntent>(
    NewsSearchState()
) {

    private var searchJob: Job? = null

    init {
        launch {
            updateUserSettings()
        }
        launch {
            getSuggestionsUseCase()
            .onEmpty {
                _state.update { it.copy(isLoading = false) }
                _events.emit(ShowError("Что-то пошло не так!"))
            }.collect { suggestions ->
                if (suggestions.isEmpty()) {
                    _state.update { it.copy(isLoading = false) }
                }
                _state.update {
                    _state.value.copy(
                        suggestions = suggestions,
                    )
                }
            }
        }
    }

    suspend fun saveSearch(query: String) {
        saveSearchUseCase(query)
    }

    suspend fun updateSearch(query: String, paramsFilter: ParamsFilter?) {
        val result = updateSearchNewsUseCase(param = query, paramsFilter = paramsFilter)
        when (result) {
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
                sendError("Успешно обновлено!")
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
                    searchNews(query = intent.param, paramsFilter = state.value.paramsFilter)
                    updateSearch(intent.param, state.value.paramsFilter).run {
                        saveSearch(intent.param)
                    }

                }

                is NewsSearchIntent.ExpandedChanged -> {
                    _state.update { it.copy(expanded = intent.expanded) }
                }

                is NewsSearchIntent.QueryChanged -> {
                    _state.update {
                        it.copy(
                            query = intent.query,
                            newsSuggestions = getNewsSuggestionsUseCase(
                                intent.query
                            ),
                            resultList = emptyList()
                        )
                    }
                }

                is NewsSearchIntent.SuggestionClicked -> {
                    searchNews(query = intent.suggestion, paramsFilter = state.value.paramsFilter)
                    updateSearch(intent.suggestion, state.value.paramsFilter).run {
                        saveSearch(intent.suggestion).run {

                        }
                    }
                }

                is NewsSearchIntent.ClearQuery -> {
                    _state.update { it.copy(query = "", expanded = false) }
                }

                is NewsSearchIntent.NewsClick -> {
                    _state.update { it.copy(query = "") }
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
                    updateUserSettingsUseCase(intent.paramsFilter.toUserSettings(intent.isDarkTheme))
                }
            }
        }
    }

    private fun ParamsFilter.toUserSettings(isDarkTheme: Boolean): UserSettings {
        return UserSettings(
            country = country?.toCountryEnum(),
            language = language?.toLanguageEnum(),
            darkTheme = isDarkTheme
        )
    }

    private fun updateUserSettings() {
        launch {
            getUserSettingsUseCase().distinctUntilChanged().onEach { settings ->
                _state.update { it.copy(userSettings = settings) }
            }.collect()
        }
    }

    private fun String.toCountryEnum(): Country? {
        return if (this.uppercase() == Country.US.name) Country.US else if (this.uppercase() == Country.RU.name) Country.RU else null
    }

    private fun String.toLanguageEnum(): Language? {
        return if (this.uppercase() == Language.EN.name) Language.EN else if (this.uppercase() == Language.RU.name) Language.RU else null
    }

    private fun searchNews(query: String, paramsFilter: ParamsFilter? = null) {
        searchJob?.cancel()

        searchJob = launch {
            searchNewsUseCase(query, paramsFilter)
                .map { list -> list.map { it.toNewsArticle() } }
                .onStart {
                    _state.update { it.copy(isLoading = true) }
                }.onEach { list ->
                    if (list.isEmpty()) {

                        _events.emit(ShowError("Ничего не найдено!"))
                    }
                    _state.update {
                        it.copy(
                            resultList = list,
                            isLoading = false,
                            expanded = false
                        )
                    }
                }.collect()
        }
    }

    override suspend fun onError(e: Throwable) {
        _state.update { it.copy(isLoading = false) }
        _events.emit(ShowError(e.message.orEmpty()))
    }
}