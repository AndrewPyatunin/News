package com.andreich.news.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.runtime.snapshotFlow
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
import com.andreich.news.presentation.core.UiMessage
import com.andreich.news.presentation.newslist.NewsListEvent
import com.andreich.news.presentation.newslist.NewsListIntent
import com.andreich.news.presentation.newslist.NewsListState
import com.andreich.news.presentation.newslist.NewsListViewModel
import com.andreich.news.ui.core.AnimatedButton
import com.andreich.news.ui.core.RadioButtonSelection
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun NewsListScreen(
    state: NewsListState,
    onClickNewsListener: (Int) -> Unit,
    onNextPageLoad: () -> Unit,
    onDismiss: () -> Unit,
    onSaveConfigClick: (UserSettings) -> Unit,
    setFabState: (NewsFabState) -> Unit,
    lazyListState: LazyListState
) {
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
    MenuPopUpItem(
        onDismiss = onDismiss, modifier = Modifier.padding(top = 8.dp), visible = state.menuExpanded
    ) {
        val RUSSIA = stringResource(R.string.russia)
        val RUSSIAN = stringResource(R.string.russian)
        val ENGLISH = stringResource(R.string.english)
        val US = stringResource(R.string.us)

        val chosenCountryLang = remember { mutableStateOf(RUSSIA to RUSSIAN) }
        val themeDark = remember { mutableStateOf(true) }
        val listCountriesToLanguages = listOf(RUSSIA to RUSSIAN, US to ENGLISH)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
        ) {
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
                        country = when (chosenCountryLang.value.first) {
                            RUSSIA -> Country.RU; US -> Country.US; else -> null
                        },
                        language = when (chosenCountryLang.value.second) {
                            RUSSIAN -> Language.RU; ENGLISH -> Language.EN; else -> null
                        },
                        darkTheme = themeDark.value
                    )
                )
            }
        }
    }
    val fabVisual = remember { mutableStateOf(false) }
    val showFab by remember(Unit) {
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
                    delay(1000.milliseconds)
                    fabVisual.value = false
                }
            }
    }
    val scope = rememberCoroutineScope()

    val scrollToTop = remember(scope) {
        {
            scope.launch {
                lazyListState.scrollToItem(0)
            }
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
    val lazyListState = rememberLazyListState()

    LaunchedEffect(state.menuExpanded) {
        onSetAppBarState(
            AppBarState(
                showFilter = state.menuExpanded,
                onFilterClick = {
                    viewModel.sendIntent(NewsListIntent.ShowMenu)
                }
            ))
    }

    LaunchedEffect(viewModel) {
        viewModel.sendIntent(NewsListIntent.LoadConfiguration)
        viewModel.sendIntent(NewsListIntent.ObserveNews)


        viewModel.events.collect {
            when (it) {
                is NewsListEvent.NavigateTo -> {
                    onNavigateToNewsDetails(it.id)
                }

                NewsListEvent.SettingsUpdated -> viewModel.sendIntent(NewsListIntent.UpdateNews)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.messages.collect {
            when (it) {
                is UiMessage.ShowError -> {
                    snackBarState.showSnackbar(
                        message = it.message,
                        duration = SnackbarDuration.Short
                    )
                }

                is UiMessage.ShowSuccess -> {
                    snackBarState.showSnackbar(
                        message = it.message,
                        duration = SnackbarDuration.Short
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
            viewModel.sendIntent(NewsListIntent.HideMenu)
        },
        onSaveConfigClick = {
            viewModel.sendIntent(NewsListIntent.ConfigureSettings(it))
        },
        setFabState = setFabState,
        lazyListState = lazyListState
    )
}