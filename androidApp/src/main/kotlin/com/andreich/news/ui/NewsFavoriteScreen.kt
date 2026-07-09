package com.andreich.news.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andreich.news.R
import com.andreich.news.ext.NewsItem
import com.andreich.news.presentation.core.UiMessage
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
    val messageSuccess = stringResource(R.string.remove_success)
    val removeMessage = stringResource(R.string.news_removed)
    val undoLabel = stringResource(R.string.undo)

    LaunchedEffect(Unit) {
        viewModel.messages.collect {
            when (it) {
                is UiMessage.ShowError -> snackbarHostState.showSnackbar(message = it.message)
                is UiMessage.ShowSuccess -> snackbarHostState.showSnackbar(message = it.message)
            }
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.sendIntent(NewsFavoriteIntent.LoadFavourites)
        viewModel.events.collect {
            when (it) {
                is NewsFavoriteEvent.NavigateToNewsDetail -> {
                    onNavigateToNewsDetails(it.newsId)
                }

                is NewsFavoriteEvent.ShowUndoRemove -> {
                    val result = snackbarHostState.showSnackbar(
                        message = removeMessage,
                        actionLabel = undoLabel,
                        withDismissAction = true
                    )

                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.sendIntent(NewsFavoriteIntent.UndoRemove(it.newsId))
                    }
                }

                NewsFavoriteEvent.RemoveSuccess -> snackbarHostState.showSnackbar(messageSuccess)
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
fun NewsFavoriteScreen(
    state: NewsFavoriteState,
    onRemoveNews: (Int) -> Unit,
    onNewsClick: (Int) -> Unit
) {
    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        items(state.news, key = { it.id }) { news ->
            val dismissState = key(news.id) { rememberSwipeToDismissBoxState() }
            LaunchedEffect(dismissState.currentValue) {
                snapshotFlow { dismissState.currentValue }
                    .distinctUntilChanged()
                    .filter { it == SwipeToDismissBoxValue.StartToEnd }
                    .collect {
                        onRemoveNews(news.id)
                        dismissState.snapTo(SwipeToDismissBoxValue.Settled)
                    }
            }
            SwipeToDismissBox(
                state = dismissState,
                enableDismissFromStartToEnd = true,
                enableDismissFromEndToStart = false,
                backgroundContent = {
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxSize()
                            .background(Color.Gray)
                    ) {
                        Text(
                            text = stringResource(R.string.remove),
                            fontSize = 24.sp,
                            color = Color.Black
                        )
                    }
                }
            ) {
                NewsItem(news) {
                    onNewsClick(news.id)
                }
            }
        }

    }
}

