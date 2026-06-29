package com.andreich.news.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andreich.news.domain.model.News
import com.andreich.news.ext.NewsItem
import com.andreich.news.presentation.newslist.NewsListEvent
import com.andreich.news.presentation.newslist.NewsListIntent
import com.andreich.news.presentation.newslist.NewsListState
import com.andreich.news.presentation.newslist.NewsListViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NewsListScreen(
    state: NewsListState,
    onClickNewsListener: (News) -> Unit,
    onNextPageLoad: () -> Unit
) {
    val lazyListState = rememberLazyListState()
    val shouldLoadMore by remember(lazyListState, state.newsList.size) {
        derivedStateOf {
            val lastVisible = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            lastVisible >= state.newsList.lastIndex - 3
        }
    }
    if (state.isLoading && state.newsList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator()
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            onNextPageLoad()
        }
    }

    LazyColumn(Modifier, state = lazyListState) {
        items(state.newsList, key = { it.id }) {
            NewsItem(it) { news ->
                onClickNewsListener(news)
            }
        }
        if (state.isLoadingNextPage) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

    }
}

@Composable
fun NewsListRoute(snackBarState: SnackbarHostState, onNavigateToNewsDetails: (News) -> Unit) {
    val viewModel: NewsListViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect {
            when (it) {
                is NewsListEvent.NavigateTo -> {
                    onNavigateToNewsDetails(it.news)
                }
                is NewsListEvent.ShowError -> {
                    snackBarState.showSnackbar(message = it.message, duration = SnackbarDuration.Long)
                }
            }
        }
    }

    NewsListScreen(state = state, onClickNewsListener = {
        viewModel.sendIntent(NewsListIntent.NewsClick(it))
    }, onNextPageLoad = {
        viewModel.sendIntent(NewsListIntent.LoadNextPage)
    })
}