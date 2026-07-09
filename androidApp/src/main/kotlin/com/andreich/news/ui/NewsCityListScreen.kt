package com.andreich.news.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.andreich.news.ext.NewsItem
import com.andreich.news.presentation.core.UiMessage
import com.andreich.news.presentation.newscitylist.NewsCityListEvent
import com.andreich.news.presentation.newscitylist.NewsCityListIntent
import com.andreich.news.presentation.newscitylist.NewsCityListState
import com.andreich.news.presentation.newscitylist.NewsCityListViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun NewsCityListRoute(
    snackbarHostState: SnackbarHostState,
    newsIds: List<Int>,
    onNewsDetailsNavigate: (Int) -> Unit
) {
    val viewModel: NewsCityListViewModel = koinViewModel(
        parameters = {
            parametersOf(newsIds)
        }
    )
    val state = viewModel.state.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.sendIntent(NewsCityListIntent.LoadNewsList(newsIds))
        viewModel.events.collect {
            when (it) {
                is NewsCityListEvent.NavigateToDetails -> {
                    onNewsDetailsNavigate(it.newsId)
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.messages.collect {
            when (it) {
                is UiMessage.ShowError -> {
                    snackbarHostState.showSnackbar(message = it.message)
                }
                is UiMessage.ShowSuccess -> {
                    snackbarHostState.showSnackbar(message = it.message)
                }
            }
        }
    }
    NewsCityListScreen(state.value) {
        viewModel.sendIntent(NewsCityListIntent.NewsClick(it))
    }
}

@Composable
fun NewsCityListScreen(state: NewsCityListState, onClickNews: (Int) -> Unit) {
    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {
        items(state.newsList, key = { it.id }) { news ->
            NewsItem(news) {
                onClickNews(news.id)
            }
        }
    }
}