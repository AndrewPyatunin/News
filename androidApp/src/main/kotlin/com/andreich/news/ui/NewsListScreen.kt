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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andreich.news.R
import com.andreich.news.domain.model.Country
import com.andreich.news.domain.model.Language
import com.andreich.news.domain.model.UserSettings
import com.andreich.news.ext.AppBarState
import com.andreich.news.ext.MenuPopUpItem
import com.andreich.news.ext.NewsFabState
import com.andreich.news.ext.NewsItem
import com.andreich.news.ext.TextContent
import com.andreich.news.ext.TextHeader
import com.andreich.news.presentation.newslist.NewsListEvent
import com.andreich.news.presentation.newslist.NewsListIntent
import com.andreich.news.presentation.newslist.NewsListState
import com.andreich.news.presentation.newslist.NewsListViewModel
import com.andreich.news.ui.core.AnimatedButton
import com.andreich.news.ui.core.RadioButtonSelection
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NewsListScreen(
    state: NewsListState,
    onClickNewsListener: (Int) -> Unit,
    onNextPageLoad: () -> Unit,
    onDismiss: () -> Unit,
    onSaveConfigClick: (UserSettings) -> Unit,
    setFabState: (NewsFabState) -> Unit
) {
    val lazyListState = rememberLazyListState()
    val shouldLoadMore by remember(lazyListState, state.newsList.size) {
        derivedStateOf {
            val lastVisible = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            lastVisible >= state.newsList.lastIndex - 5
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            onNextPageLoad()
        }
    }
    if (state.menuExpanded) {
        MenuPopUpItem(
            onDismiss = onDismiss, modifier = Modifier.padding(top = 8.dp),
        ) {
            val NONE = stringResource(R.string.no)
            val RUSSIA = stringResource(R.string.russia)
            val RUSSIAN = stringResource(R.string.russian)
            val ENGLISH = stringResource(R.string.english)
            val US = stringResource(R.string.us)
            val WITHOUT = stringResource(R.string.without_param)

            val chosenCountryLang = remember { mutableStateOf(WITHOUT to WITHOUT) }
            val themeDark = remember { mutableStateOf(true) }
            val listCountriesToLanguages = listOf(RUSSIA to RUSSIAN, US to ENGLISH)
            Column(modifier = Modifier.fillMaxSize().verticalScroll(state = rememberScrollState())) {
                TextHeader("${stringResource(R.string.news_country)}\n(${stringResource(R.string.response_language)})")
                HorizontalDivider()
                RadioButtonSelection(radioOptions = listCountriesToLanguages, onParam = {
                    chosenCountryLang.value = it
                }) { text ->
                    Text(
                        fontSize = 18.sp,
                        text = " ${stringResource(R.string.country)} ${text.first} (${text.second} ${
                            stringResource(
                                R.string.language
                            )
                        })",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
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
                AnimatedButton(text = stringResource(R.string.configure)) {
                    onSaveConfigClick(
                        UserSettings(
                            country = when(chosenCountryLang.value.first) { RUSSIA -> Country.RU; US -> Country.US; else -> null },
                            language = when(chosenCountryLang.value.second) { RUSSIAN -> Language.RU; ENGLISH -> Language.EN; else -> null },
                            darkTheme = themeDark.value
                        )
                    )
                }
            }
        }
    }
    val showFab by remember(state) {
        derivedStateOf {
            lazyListState.lastScrolledBackward || (!lazyListState.canScrollForward && !lazyListState.canScrollBackward)

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
    LazyColumn(
        Modifier.background(MaterialTheme.colorScheme.background),
        state = lazyListState
    ) {


        if (state.isLoading && state.newsList.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
        items(state.newsList, key = { it.id }) {
            NewsItem(it) {
                onClickNewsListener(it.id)
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
    onNavigateToNewsDetails: (Int) -> Unit,
    onSetAppBarState: (AppBarState) -> Unit,
    setFabState: (NewsFabState) -> Unit
) {
    val viewModel: NewsListViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.sendIntent(NewsListIntent.LoadConfiguration)
        viewModel.sendIntent(NewsListIntent.ObserveNews)
        viewModel.sendIntent(NewsListIntent.UpdateNews)
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
                    onNavigateToNewsDetails(it.id)
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
        },
        setFabState = setFabState
    )
}