package com.andreich.news.presentation.newslist

import com.andreich.news.domain.model.RequestResult
import com.andreich.news.domain.usecase.GetUserSettingsUseCase
import com.andreich.news.domain.usecase.LoadNewsListUseCase
import com.andreich.news.domain.usecase.UpdateNewsUseCase
import com.andreich.news.domain.usecase.UpdateUserSettingsUseCase
import com.andreich.news.presentation.core.BaseViewModel
import com.andreich.news.presentation.core.UiMessage
import com.andreich.news.presentation.core.toNewsArticle
import com.andreich.news.presentation.newslist.NewsListEvent.NavigateTo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update

class NewsListViewModel(
    private val loadNewsListUseCase: LoadNewsListUseCase,
    private val updateUserSettingsUseCase: UpdateUserSettingsUseCase,
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
    private val updateNewsUseCase: UpdateNewsUseCase
) : BaseViewModel<NewsListState, NewsListEvent, NewsListIntent>(NewsListState()) {

    private var lastError: String? = null
    private val PAGE_SIZE = 12
    private var limit = MutableStateFlow(PAGE_SIZE)

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun observeNews() {
        launch {
            _state.mapNotNull { it.userSettings }
                .distinctUntilChanged()
                .combine(limit) { settings, limit ->
                    Triple(settings.language?.name?.lowercase(), settings.country?.name?.lowercase(), limit)
                }
                .flatMapLatest { (language, country, limit) ->
                    loadNewsListUseCase(language, country, limit)
                }.map { list -> list.map { it.toNewsArticle() } }.onStart {
                    _state.update { it.copy(isLoading = true) }
                }.onEach { list ->
                    _state.update { it.copy(newsList = list, isLoading = false) }
                }.onEmpty {
                    _state.update { it.copy(isLoading = false) }
                    _messages.emit(UiMessage.ShowError("Новостей нет!"))
                }.collect()
        }

    }

    private fun updateNews() {
        launch {
            val settings = state.value.userSettings
            settings?.let {
                when (val result = updateNewsUseCase(it.language?.name ?: "ru", it.country?.name ?: "ru")) {
                    is RequestResult.Failure.NoInternet -> {
                        onRequestError(/*result.message*/ "Отсутствует интернет!")
                    }
                    is RequestResult.Failure.Serialization -> {
                        onRequestError(result.message)
                    }
                    is RequestResult.Failure.Server -> {
                        onRequestError(/*result.message*/"Серверная Ошибка")
                    }
                    is RequestResult.Failure.Timeout -> {
                        onRequestError(result.message)
                    }
                    is RequestResult.Failure.Unauthorized -> {
                        onRequestError(/*result.message*/"Пользователь не авторизован!")
                    }
                    is RequestResult.Failure.Unknown -> {
                        onRequestError(/*result.message*/"У вас не осталось токенов, обновите план!")
                    }
                    RequestResult.Success -> {
                        _messages.emit(UiMessage.ShowSuccess("Новости успешно обновлены!"))
                    }
            }

            }
        }
    }

    private fun loadNextPage() {
        limit.update { it + PAGE_SIZE }
        if (limit.value > state.value.newsList.size) {
            _state.update {
                it.copy(isLoadingNextPage = false)
            }
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
                    _events.emit(NavigateTo(intent.newsId))

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

                is NewsListIntent.LoadConfiguration -> loadUserSettingsUseCase()
                is NewsListIntent.ObserveNews -> observeNews()
                is NewsListIntent.UpdateNews -> updateNews()
            }
        }

    }

    private suspend fun onRequestError(error: String) {
        if (lastError == error) return
        lastError = error
        _messages.emit(UiMessage.ShowError(error))
    }

    private fun loadUserSettingsUseCase() {
        launch {
            getUserSettingsUseCase().onEach { settings ->
                _state.update {
                    it.copy(userSettings = settings)
                }
                _events.emit(NewsListEvent.SettingsUpdated)
            }.collect()
        }
    }

    override suspend fun onError(e: Throwable) {
        _state.update { it.copy(isLoading = false) }
        _messages.emit(UiMessage.ShowError(e.message.orEmpty()))
    }
}