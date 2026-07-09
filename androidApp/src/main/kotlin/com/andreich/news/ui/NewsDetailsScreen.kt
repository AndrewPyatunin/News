package com.andreich.news.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.andreich.news.R
import com.andreich.news.ext.NewsFabState
import com.andreich.news.presentation.core.UiMessage
import com.andreich.news.presentation.newsdetail.NewsDetailsIntent
import com.andreich.news.presentation.newsdetail.NewsDetailsState
import com.andreich.news.presentation.newsdetail.NewsDetailsViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun NewsDetailScreen(
    modifier: Modifier,
    state: NewsDetailsState,
    onAddToFavoriteClick: () -> Unit,
    onRemoveFromFavoriteClick: () -> Unit,
    setFabState: (NewsFabState) -> Unit
) {
    val lazyListState = rememberLazyListState()
    var visibleChunks by remember(state.chunks) {
        mutableIntStateOf(1)
    }
    val fabVisual = remember { mutableStateOf(false) }
    val showFab by remember(state) {
        derivedStateOf {
            lazyListState.lastScrolledBackward && fabVisual.value

        }
    }
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.isScrollInProgress }
            .collectLatest { scrolling ->
                if (scrolling) {
                    fabVisual.value = true
                } else {
                    delay(1500.milliseconds)
                    fabVisual.value = false
                }
            }
    }
    val scope = rememberCoroutineScope()

    val scrollToTop = {
        scope.launch {
            lazyListState.scrollToItem(0)
        }
    }
    LaunchedEffect(showFab) {
        setFabState(NewsFabState(showFab, scrollToTop))
    }
    DisposableEffect(Unit) {
        onDispose {
            setFabState(NewsFabState(false, { Job() }))
        }
    }
    LaunchedEffect(state.chunks) {
        while (visibleChunks < state.chunks.size) {
            withFrameNanos { }
            visibleChunks = minOf(
                visibleChunks + 5,
                state.chunks.size
            )
        }
    }
    LazyColumn(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        state = lazyListState
    ) {
        if (state.isLoading) {
            item {
                CircularProgressIndicator()
            }

        } else {
            val news = state.news
            item {
                Text(
                    text = news?.title.orEmpty(),
                    fontSize = 24.sp,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(horizontal = 6.dp),
                )
                Spacer(modifier = Modifier.size(8.dp))
            }
            item {
                Box(
                    contentAlignment = Alignment.Center, modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth()
                ) {
                    if (news?.imageUrl != "") {
                        AsyncImage(
                            model = news?.imageUrl,
                            modifier = Modifier.fillMaxWidth(),
                            contentDescription = null,
                            placeholder = painterResource(R.drawable.news_placeholder),
                            error = painterResource(R.drawable.news_placeholder)
                        )
                    } else Image(
                        painter = painterResource(R.drawable.news_placeholder),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth()
                    )

                    IconButton(
                        modifier = Modifier
                            .padding(6.dp)
                            .size(28.dp)
                            .align(Alignment.TopEnd),
                        onClick = {
                            if (state.isFavorite) onRemoveFromFavoriteClick()
                            else onAddToFavoriteClick()
                        }) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(R.drawable.favorite_filled),
                                contentDescription = null,
                                tint = Color.Gray
                            )
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = if (!state.isFavorite)
                                    painterResource(R.drawable.favorite_24px) else painterResource(R.drawable.favorite_true),
                                contentDescription = null,
                                tint = if (state.isFavorite) Color.Red else Color.Unspecified
                            )
                        }

                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    news?.publishedAt?.let {
                        Text(
                            text = stringResource(R.string.published_at, it),
                            textAlign = TextAlign.Left,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                    news?.author?.let {
                        Text(
                            text = stringResource(R.string.author, it),
                            textAlign = TextAlign.Right,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }
            item {

                Spacer(modifier = Modifier.size(8.dp))
            }
        }
        items(state.chunks.take(visibleChunks)) { chunk ->

            Text(
                text = chunk,
                fontSize = 16.sp,
                modifier = modifier.padding(horizontal = 4.dp)
            )
        }
    }

}

@Composable
fun NewsDetailsRoute(snackBarState: SnackbarHostState, newsId: Int, setFabState: (NewsFabState) -> Unit) {
    val viewModel: NewsDetailsViewModel = koinViewModel<NewsDetailsViewModel>(parameters = {
        parametersOf(newsId)
    })
    val state by viewModel.state.collectAsState()
    val addMessage = stringResource(R.string.added_to_favorite)
    val removeMessage = stringResource(R.string.removed_from_favorite)

    LaunchedEffect(viewModel) {
        viewModel.sendIntent(NewsDetailsIntent.LoadNews(newsId))
        viewModel.messages.collect {
            when (it) {
                is UiMessage.ShowError -> snackBarState.showSnackbar(message = it.message)
                is UiMessage.ShowSuccess -> snackBarState.showSnackbar(message = it.message)
            }
        }
    }

    NewsDetailScreen(
        modifier = Modifier,
        state = state,
        onAddToFavoriteClick = {
            viewModel.sendIntent(NewsDetailsIntent.AddToFavorite(addMessage))
        }, onRemoveFromFavoriteClick = {
            viewModel.sendIntent(NewsDetailsIntent.RemoveFromFavorite(removeMessage))
        }, setFabState = setFabState
    )
}