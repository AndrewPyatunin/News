package com.andreich.news.presentation.newslist

import androidx.lifecycle.viewModelScope
import com.andreich.news.domain.model.News
import com.andreich.news.domain.usecase.LoadNewsListUseCase
import com.andreich.news.presentation.core.BaseViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update

class NewsListViewModel(
    loadNewsListUseCase: LoadNewsListUseCase
) : BaseViewModel<NewsListState, NewsListEvent, NewsListIntent>(NewsListState()) {

    private val PAGE_SIZE = 15

    private var fullNews: List<News> = emptyList()

    private var visibleCount = PAGE_SIZE

    init {
        loadNewsListUseCase().onStart {
            _state.update { it.copy(isLoading = false) }
        }.onEach { list ->
            fullNews = list
            _state.update { it.copy(newsList = fullNews.take(PAGE_SIZE), isLoading = false) }
        }.catch {
            _events.emit(NewsListEvent.ShowError(it.message.orEmpty()))
        }
            .launchIn(viewModelScope)
    }

    private fun loadNextPage() {
        if (visibleCount >= fullNews.size) return
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
                    _events.emit(NewsListEvent.NavigateTo(intent.news))
            }
        }

    }

    override suspend fun onError(e: Throwable) {
        _events.emit(NewsListEvent.ShowError(e.message.orEmpty()))
    }
}