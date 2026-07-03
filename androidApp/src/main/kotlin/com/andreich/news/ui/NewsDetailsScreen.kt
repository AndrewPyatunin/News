package com.andreich.news.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.andreich.news.R
import com.andreich.news.presentation.newsdetail.NewsDetailsEvent
import com.andreich.news.presentation.newsdetail.NewsDetailsIntent
import com.andreich.news.presentation.newsdetail.NewsDetailsState
import com.andreich.news.presentation.newsdetail.NewsDetailsViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun NewsDetailScreen(
    modifier: Modifier,
    state: NewsDetailsState,
    onAddToFavoriteClick: () -> Unit,
    onRemoveFromFavoriteClick: () -> Unit
) {
    val news = state.news
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = news?.title.orEmpty(),
            fontSize = 24.sp,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(horizontal = 6.dp),
        )
        Spacer(modifier = Modifier.size(8.dp))
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
        ) {
            Image(
                painter = rememberAsyncImagePainter(news?.imageUrl),
                modifier = Modifier,
                contentDescription = null
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
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
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
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = news?.content.orEmpty(), fontSize = 16.sp)
    }
}

@Composable
fun NewsDetailsRoute(snackBarState: SnackbarHostState, newsId: Int) {
    val viewModel: NewsDetailsViewModel = koinViewModel<NewsDetailsViewModel>(parameters = {
        parametersOf(newsId)
    })
    val state by viewModel.state.collectAsState()
    val addMessage = stringResource(R.string.added_to_favorite)
    val removeMessage = stringResource(R.string.removed_from_favorite)

    LaunchedEffect(Unit) {
        viewModel.sendIntent(NewsDetailsIntent.LoadNews(newsId))
        viewModel.events.collect {
            when (it) {
                is NewsDetailsEvent.AddToFavoriteSuccess -> {
                    snackBarState.showSnackbar(message = it.message)
                }

                is NewsDetailsEvent.ShowError -> {
                    snackBarState.showSnackbar(message = it.message)
                }

                is NewsDetailsEvent.RemoveFromFavoriteSuccess -> {
                    snackBarState.showSnackbar(message = it.message)
                }
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
        }
    )
}