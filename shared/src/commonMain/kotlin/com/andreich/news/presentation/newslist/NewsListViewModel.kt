package com.andreich.news.presentation.newslist

import com.andreich.news.domain.model.News
import com.andreich.news.domain.usecase.GetUserSettingsUseCase
import com.andreich.news.domain.usecase.LoadNewsListUseCase
import com.andreich.news.domain.usecase.UpdateUserSettingsUseCase
import com.andreich.news.presentation.core.BaseViewModel
import com.andreich.news.presentation.newslist.NewsListEvent.NavigateTo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlin.time.Duration.Companion.milliseconds

class NewsListViewModel(
    loadNewsListUseCase: LoadNewsListUseCase,
    private val updateUserSettingsUseCase: UpdateUserSettingsUseCase,
    private val getUserSettingsUseCase: GetUserSettingsUseCase
) : BaseViewModel<NewsListState, NewsListEvent, NewsListIntent>(NewsListState()) {

    private val PAGE_SIZE = 10

    private var fullNews: List<News> = emptyList()

    private var visibleCount = PAGE_SIZE

    init {
        launch {
            loadNewsListUseCase().onStart {
                _state.update { it.copy(isLoading = true) }
            }.onEach { list ->
                fullNews = list
                _state.update { it.copy(newsList = fullNews.take(PAGE_SIZE), isLoading = false) }
            }.onEmpty {
                _state.update { it.copy(isLoading = false) }
                _events.emit(NewsListEvent.ShowError("Новостей нет!"))
            }.catch {
                _events.emit(NewsListEvent.ShowError(it.message.orEmpty()))
            }.collect()
        }
    }

    private suspend fun loadNextPage() {
        if (visibleCount >= fullNews.size) {
            _state.update {
                it.copy(
                    isLoadingNextPage = false,
                )
            }
            return
        }
        delay(500.milliseconds)
        visibleCount = minOf(fullNews.size, visibleCount + PAGE_SIZE)
        _state.update {
            it.copy(
                newsList = fullNews.take(visibleCount),
                isLoadingNextPage = false,
            )
        }
    }

    override fun sendIntent(intent: NewsListIntent) {
        launch {
            when (intent) {
                NewsListIntent.LoadNextPage -> {
                    _state.update {
                        it.copy(isLoadingNextPage = true)
                    }
                    loadNextPage()
                }

                is NewsListIntent.NewsClick ->
                    _events.emit(NavigateTo(intent.news))

                NewsListIntent.ShowMenu -> {
                    _state.update {
                        it.copy(menuExpanded = !it.menuExpanded)
                    }
                }

                is NewsListIntent.ConfigureSettings -> {
                    updateUserSettingsUseCase(intent.userSettings)
                    _state.update {
                        it.copy(menuExpanded = false)
                    }
                }

                NewsListIntent.LoadConfiguration -> loadUserSettingsUseCase()
            }
        }

    }

    private fun loadUserSettingsUseCase() {
        launch {
            getUserSettingsUseCase().onEach { settings ->
                _state.update {
                    it.copy(userSettings = settings)
                }
            }.collect()
        }
    }

    override suspend fun onError(e: Throwable) {
        _state.update { _state.value.copy(isLoading = false) }
        _events.emit(NewsListEvent.ShowError(e.message.orEmpty()))
    }
}