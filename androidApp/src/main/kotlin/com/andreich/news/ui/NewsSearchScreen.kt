package com.andreich.news.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.andreich.news.R
import com.andreich.news.domain.model.News
import com.andreich.news.ext.NewsItem
import com.andreich.news.presentation.newssearch.NewsSearchEvent
import com.andreich.news.presentation.newssearch.NewsSearchIntent
import com.andreich.news.presentation.newssearch.NewsSearchState
import com.andreich.news.presentation.newssearch.NewsSearchViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsSearchScreen(
    state: NewsSearchState,
    onIntent: (NewsSearchIntent) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        border = BorderStroke(1.dp, color = Color.Black),
        shape = CardDefaults.elevatedShape,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        SearchBar(
            modifier = Modifier,
            inputField = {
                SearchBarDefaults.InputField(
                    query = state.query.trim(),
                    expanded = state.expanded,
                    onQueryChange = {
                        onIntent(NewsSearchIntent.QueryChanged(it.trim()))
                    },
                    onExpandedChange = {
                        onIntent(NewsSearchIntent.ExpandedChanged(it))
                    },
                    onSearch = {
                        onIntent(NewsSearchIntent.SearchNews(it.trim()))
                    },
                    placeholder = { Text("Поиск") },
                    trailingIcon = {
                        if (state.query.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    onIntent(NewsSearchIntent.ClearQuery)
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.close_24px),
                                    contentDescription = "clear"
                                )
                            }
                        }
                    }
                )
            },
            expanded = state.expanded,
            onExpandedChange = {
                onIntent(NewsSearchIntent.ExpandedChanged(it))
            }

        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator()
                }

                state.expanded -> {
                    state.suggestions.forEach {
                        ListItem(headlineContent = {
                            Text(it)
                        })
                    }
                }

                else -> {
                    val lazyListState = rememberLazyListState()
                    LazyColumn(state = lazyListState) {
                        items(state.resultList) { news ->
                            NewsItem(news) {
                                onIntent(NewsSearchIntent.NewsClick(it))
                            }
                        }
                    }
                }
            }

        }
    }

}

@Composable
fun NewsSearchRoute(snackbarState: SnackbarHostState, onNavigateToNewsDetails: (News) -> Unit) {
    val viewModel: NewsSearchViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect {
            when (it) {
                is NewsSearchEvent.ShowError -> snackbarState.showSnackbar(message = it.message)
                is NewsSearchEvent.NavigateTo -> onNavigateToNewsDetails(it.news)
            }
        }
    }
    NewsSearchScreen(
        state = state,
        onIntent = {
            when (it) {
                NewsSearchIntent.ClearQuery -> {
                    viewModel.sendIntent(NewsSearchIntent.ClearQuery)
                }

                is NewsSearchIntent.ExpandedChanged -> {
                    viewModel.sendIntent(NewsSearchIntent.ExpandedChanged(it.expanded))
                }

                is NewsSearchIntent.QueryChanged -> {
                    viewModel.sendIntent(NewsSearchIntent.QueryChanged(it.query))
                }

                is NewsSearchIntent.SearchNews -> {
                    viewModel.sendIntent(NewsSearchIntent.SearchNews(it.param))
                }

                is NewsSearchIntent.SuggestionClicked -> {
                    viewModel.sendIntent(NewsSearchIntent.SuggestionClicked(it.suggestion))
                }

                is NewsSearchIntent.NewsClick -> {
                    viewModel.sendIntent(NewsSearchIntent.NewsClick(news = it.news))
                }
            }
        }
    )
}