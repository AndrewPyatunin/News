package com.andreich.news.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andreich.news.domain.model.News
import com.andreich.news.ext.NewsItem
import com.andreich.news.presentation.newsfavorite.NewsFavoriteEvent
import com.andreich.news.presentation.newsfavorite.NewsFavoriteIntent
import com.andreich.news.presentation.newsfavorite.NewsFavoriteState
import com.andreich.news.presentation.newsfavorite.NewsFavoriteViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NewsFavoriteRoute(
    snackbarHostState: SnackbarHostState,
    onNavigateToNewsDetails: (Int) -> Unit
) {
    val viewModel: NewsFavoriteViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()


    LaunchedEffect(Unit) {
        viewModel.sendIntent(NewsFavoriteIntent.LoadFavourites)
        viewModel.events.collect {
            when (it) {
                is NewsFavoriteEvent.NavigateToNewsDetail -> {
                    onNavigateToNewsDetails(it.news.id)
                }
                NewsFavoriteEvent.RemoveSuccess -> {
                    snackbarHostState.showSnackbar(message = "Remove success")
                }
                is NewsFavoriteEvent.ShowError -> {
                    snackbarHostState.showSnackbar(message = it.message)
                }

                is NewsFavoriteEvent.ShowUndoRemove -> {
                    val result = snackbarHostState.showSnackbar(
                        message = "News removed",
                        actionLabel = "Undo",
                        withDismissAction = true
                    )

                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.sendIntent(NewsFavoriteIntent.UndoRemove(it.news))
                    }
                }
            }
        }
    }

    NewsFavoriteScreen(state = state, onRemoveNews = {
        viewModel.sendIntent(NewsFavoriteIntent.RemoveNews(it))
    }, onNewsClick = {
        viewModel.sendIntent(NewsFavoriteIntent.ClickNews(it))
    })

}

@Composable
fun NewsFavoriteScreen(state: NewsFavoriteState, onRemoveNews: (News) -> Unit, onNewsClick: (News) -> Unit) {
        LazyColumn(Modifier.fillMaxSize()) {
            items(state.news, key = { it.id }) { news ->
                val dismissState = key(news.id) { rememberSwipeToDismissBoxState() }
                LaunchedEffect(dismissState.currentValue) {
                    snapshotFlow { dismissState.currentValue }
                        .distinctUntilChanged()
                        .filter { it == SwipeToDismissBoxValue.StartToEnd }
                        .collect {
                            onRemoveNews(news)
                            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
                        }
                }
                SwipeToDismissBox(
                    state = dismissState,
                    enableDismissFromStartToEnd = true,
                    enableDismissFromEndToStart = false,
                    backgroundContent = {
                        Box(modifier = Modifier.padding(16.dp).fillMaxSize().background(Color.Gray)) {
                            Text(text = "Удалить", fontSize = 24.sp, color = Color.Black)
                        }
                    }
                ) {
                    NewsItem(news) {
                        onNewsClick(it)
                    }
                }
            }
        }
}

