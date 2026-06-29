package com.andreich.news.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontFamily
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
fun NewsDetailScreen(modifier: Modifier, state: NewsDetailsState, onAddToFavoriteClick: () -> Unit) {
    val news = state.news
    Column(modifier.fillMaxSize()) {
        Text(text = news?.title.orEmpty(), fontSize = 26.sp)
        Spacer(modifier = Modifier.size(8.dp))
        Box(contentAlignment = Alignment.Center,modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()) {
            Image(
                painter = rememberAsyncImagePainter(news?.imageUrl),
                modifier = Modifier,
                contentDescription = null
            )
            IconButton(
                modifier = Modifier
                    .padding(6.dp)
                    .size(24.dp)
                    .align(Alignment.TopEnd),
                onClick = {
                    onAddToFavoriteClick()
            }) {
                Icon(
                    painter = painterResource(R.drawable.favorite_24px),
                    contentDescription = null,
                    tint = Color.White,
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
    viewModel.sendIntent(NewsDetailsIntent.LoadNews(newsId))

    LaunchedEffect(Unit) {
        viewModel.events.collect {
            when (it) {
                is NewsDetailsEvent.AddToFavoriteSuccess -> {
                    snackBarState.showSnackbar(message = it.message)
                }
                is NewsDetailsEvent.ShowError -> {
                    snackBarState.showSnackbar(message = it.message)
                }
            }
        }
    }

    NewsDetailScreen(
        modifier = Modifier,
        state = state,
        onAddToFavoriteClick = {
            viewModel.sendIntent(NewsDetailsIntent.AddToFavorite)
        }
    )
}