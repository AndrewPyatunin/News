package com.andreich.news.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.andreich.news.R
import com.andreich.news.domain.model.Country
import com.andreich.news.domain.model.Language
import com.andreich.news.domain.model.News
import com.andreich.news.domain.model.UserSettings
import com.andreich.news.ext.AppBarState
import com.andreich.news.ext.MenuPopUpItem
import com.andreich.news.ext.NewsItem
import com.andreich.news.ext.TextContent
import com.andreich.news.ext.TextHeader
import com.andreich.news.presentation.newslist.NewsListEvent
import com.andreich.news.presentation.newslist.NewsListIntent
import com.andreich.news.presentation.newslist.NewsListState
import com.andreich.news.presentation.newslist.NewsListViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NewsListScreen(
    state: NewsListState,
    onClickNewsListener: (News) -> Unit,
    onNextPageLoad: () -> Unit,
    onDismiss: () -> Unit,
    onSaveConfigClick: (UserSettings) -> Unit
) {
    val lazyListState = rememberLazyListState()
    val shouldLoadMore by remember(lazyListState, state.newsList.size) {
        derivedStateOf {
            val lastVisible = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            lastVisible >= state.newsList.lastIndex - 3
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            onNextPageLoad()
        }
    }
    if (state.menuExpanded) {
        MenuPopUpItem(
            onDismiss = onDismiss, modifierCard = Modifier.padding(top = 8.dp),
        ) {
            val countryUs = remember { mutableStateOf(true) }
            val languageEn = remember { mutableStateOf(true) }
            val themeDark = remember { mutableStateOf(true) }
            Column(modifier = Modifier.fillMaxSize()) {
                TextHeader(stringResource(R.string.news_country))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextContent(stringResource(R.string.russia))
                    Switch(
                        checked = countryUs.value,
                        onCheckedChange = {
                            countryUs.value = it
                        }
                    )
                    TextContent(stringResource(R.string.us))
                }
                HorizontalDivider()
                TextHeader(stringResource(R.string.response_language))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextContent(stringResource(R.string.russian))
                    Switch(
                        checked = languageEn.value,
                        onCheckedChange = {
                            languageEn.value = it
                        }
                    )
                    TextContent(stringResource(R.string.english))
                }
                HorizontalDivider()
                TextHeader(stringResource(R.string.theme))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextContent(stringResource(R.string.light))
                    Switch(
                        checked = themeDark.value,
                        onCheckedChange = {
                            themeDark.value = it
                        }
                    )
                    TextContent(stringResource(R.string.dark))
                }
                Button(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .fillMaxWidth(), onClick = {
                        onSaveConfigClick(
                            UserSettings(
                                country = if (countryUs.value) Country.US else Country.RU,
                                language = if (languageEn.value) Language.EN else Language.RU,
                                darkTheme = themeDark.value
                            )
                        )
                    }) {
                    TextHeader(stringResource(R.string.configure))
                }
            }
        }
    }

    LazyColumn(
        Modifier.background(MaterialTheme.colorScheme.background),
        state = lazyListState
    ) {

        if (state.isLoading && state.newsList.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
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
fun NewsListRoute(
    snackBarState: SnackbarHostState,
    onNavigateToNewsDetails: (News) -> Unit,
    onSetAppBarState: (AppBarState) -> Unit
) {
    val viewModel: NewsListViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sendIntent(NewsListIntent.LoadConfiguration)
        onSetAppBarState(
            AppBarState(
                showFilter = state.menuExpanded,
                onFilterClick = {
                    viewModel.sendIntent(NewsListIntent.ShowMenu)
                }
            ))
        viewModel.events.collect {
            when (it) {
                is NewsListEvent.NavigateTo -> {
                    onNavigateToNewsDetails(it.news)
                }

                is NewsListEvent.ShowError -> {
                    snackBarState.showSnackbar(
                        message = it.message,
                        duration = SnackbarDuration.Long
                    )
                }
            }
        }
    }

    NewsListScreen(
        state = state,
        onClickNewsListener = {
            viewModel.sendIntent(NewsListIntent.NewsClick(it))
        }, onNextPageLoad = {
            viewModel.sendIntent(NewsListIntent.LoadNextPage)
        }, onDismiss = {
            viewModel.sendIntent(NewsListIntent.ShowMenu)
        },
        onSaveConfigClick = {
            viewModel.sendIntent(NewsListIntent.ConfigureSettings(it))
        })
}