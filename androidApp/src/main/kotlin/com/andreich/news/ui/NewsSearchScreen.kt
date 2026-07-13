package com.andreich.news.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andreich.news.R
import com.andreich.news.domain.model.ParamsFilter
import com.andreich.news.ext.MenuPopUpItem
import com.andreich.news.ext.NewsItem
import com.andreich.news.ext.TextContent
import com.andreich.news.ext.TextHeader
import com.andreich.news.presentation.core.UiMessage
import com.andreich.news.presentation.newssearch.NewsSearchEvent
import com.andreich.news.presentation.newssearch.NewsSearchIntent
import com.andreich.news.presentation.newssearch.NewsSearchIntent.ClearQuery
import com.andreich.news.presentation.newssearch.NewsSearchIntent.ExpandedChanged
import com.andreich.news.presentation.newssearch.NewsSearchIntent.FilterMenuClick
import com.andreich.news.presentation.newssearch.NewsSearchIntent.NewsClick
import com.andreich.news.presentation.newssearch.NewsSearchIntent.QueryChanged
import com.andreich.news.presentation.newssearch.NewsSearchIntent.SaveFilterParams
import com.andreich.news.presentation.newssearch.NewsSearchIntent.SearchNews
import com.andreich.news.presentation.newssearch.NewsSearchIntent.SuggestionClicked
import com.andreich.news.presentation.newssearch.NewsSearchState
import com.andreich.news.presentation.newssearch.NewsSearchViewModel
import com.andreich.news.ui.core.AnimatedButton
import com.andreich.news.ui.core.RadioButtonSelection
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsSearchScreen(
    state: NewsSearchState,
    onIntent: (NewsSearchIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val query = remember { mutableStateOf("") }
        SearchBar(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 4.dp,
            shadowElevation = 4.dp,
            inputField = {
                SearchBarDefaults.InputField(
                    query = query.value,
                    expanded = state.expanded,
                    onQueryChange = {
                        query.value = it
                        onIntent(QueryChanged(it))
                    },
                    onExpandedChange = {
                        onIntent(ExpandedChanged(it))
                    },
                    onSearch = {
                        onIntent(SearchNews(it.trim()))
                        onIntent(ClearQuery)
                    },
                    placeholder = { Text(stringResource(R.string.search)) },
                    trailingIcon = {
                        Row {
                            IconButton(
                                onClick = {
                                    onIntent(FilterMenuClick)
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_tune_24),
                                    contentDescription = null
                                )
                            }
                            if (state.query.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        query.value = ""
                                        onIntent(ClearQuery)
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.close_24px),
                                        contentDescription = null
                                    )
                                }
                            }
                        }

                    },
                )
            },
            expanded = state.expanded,
            onExpandedChange = {
                onIntent(ExpandedChanged(it))
            }

        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator()
                }

                state.expanded -> {
                    val suggestions =
                        if (state.query.trim() != "") state.newsSuggestions else state.suggestions
                    suggestions.forEach {
                        ListItem(
                            headlineContent = {
                                Text(it, overflow = TextOverflow.Ellipsis, maxLines = 2)
                            },
                            leadingContent = {
                                Icon(
                                    modifier = Modifier.size(18.dp),
                                    painter = painterResource(R.drawable.search_24px),
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier.clickable {
                                onIntent(SuggestionClicked(it))
                            })
                    }
                }

                !state.expanded -> {

                }
            }

        }
            NewsPopUp(onSaveFilterParam = {
                onIntent(SaveFilterParams(it))
            }, onDismiss = {
                onIntent(FilterMenuClick)
            }, state = state)
        }
        val lazyListState = rememberLazyListState()
        if (state.resultList.isNotEmpty()) {
            LazyColumn(state = lazyListState) {
                items(state.resultList, key = { it.id }) { news ->
                    NewsItem(news) {
                        onIntent(NewsClick(news.id))
                    }
                }
            }
        }
}

@Composable
fun NewsSearchRoute(snackbarState: SnackbarHostState, onNavigateToNewsDetails: (Int) -> Unit) {
    val viewModel: NewsSearchViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.messages.collect {
            when (it) {
                is UiMessage.ShowError -> snackbarState.showSnackbar(it.message)
                is UiMessage.ShowSuccess -> snackbarState.showSnackbar(it.message)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect {
            when (it) {
                is NewsSearchEvent.NavigateTo -> onNavigateToNewsDetails(it.newsId)
            }
        }
    }
    NewsSearchScreen(
        state = state,
        onIntent = {
            when (it) {
                ClearQuery -> {
                    viewModel.sendIntent(ClearQuery)
                }

                is ExpandedChanged -> {
                    viewModel.sendIntent(ExpandedChanged(it.expanded))
                }

                is QueryChanged -> {
                    viewModel.sendIntent(QueryChanged(it.query))
                }

                is SearchNews -> {
                    viewModel.sendIntent(SearchNews(it.param))
                }

                is SuggestionClicked -> {
                    viewModel.sendIntent(SuggestionClicked(it.suggestion))
                }

                is NewsClick -> {
                    viewModel.sendIntent(NewsClick(newsId = it.newsId))
                }

                is FilterMenuClick -> {
                    viewModel.sendIntent(FilterMenuClick)
                }

                is SaveFilterParams -> {
                    viewModel.sendIntent(SaveFilterParams(it.paramsFilter))
                }
            }
        }
    )
}


@Composable
fun NewsPopUp(onSaveFilterParam: (ParamsFilter) -> Unit, onDismiss: () -> Unit, state: NewsSearchState) {
    val scrollableState = rememberScrollState()

    val RUSSIA = stringResource(R.string.russia)
    val RUSSIAN = stringResource(R.string.russian)
    val ENGLISH = stringResource(R.string.english)
    val US = stringResource(R.string.us)
    val WITHOUT = stringResource(R.string.without_param)
    val NONE = stringResource(R.string.none)
    val listCountriesToLanguages: List<Pair<String, String>> = listOf(
        WITHOUT to WITHOUT,
        RUSSIA to RUSSIAN,
        US to ENGLISH,
    )
    val visible = state.popUpMenuShowed
    MenuPopUpItem(alignment = Alignment.BottomCenter, onDismiss = onDismiss, visible = visible) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    state = scrollableState
                )
        ) {
            val selectedCategory = remember { mutableStateOf(NONE to NONE) }
            val selectedOptionCountry = remember { mutableStateOf(WITHOUT to WITHOUT) }
            TextHeader("${stringResource(R.string.news_country)}\n(${stringResource(R.string.response_language)})")
            HorizontalDivider()
            RadioButtonSelection(radioOptions = listCountriesToLanguages, onParam = {
                selectedOptionCountry.value = it
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
            val categories = listOf(
                "none",
                "politics",
                "sports",
                "business",
                "technology",
                "health",
                "science"
            )
            val localizedCategories = listOf(
                stringResource(R.string.no),
                stringResource(R.string.politics),
                stringResource(R.string.sports),
                stringResource(R.string.business),
                stringResource(R.string.technology),
                stringResource(R.string.health),
                stringResource(R.string.science)
            )
            val categoriesChoose =
                localizedCategories.mapIndexed { index, category -> category to categories[index] }
            TextHeader(stringResource(R.string.category_search))
            RadioButtonSelection(radioOptions = categoriesChoose, onParam = {
                selectedCategory.value = it
            }) { text ->
                Text(
                    fontSize = 16.sp,
                    text = text.first,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            HorizontalDivider()
            TextHeader(stringResource(R.string.location_search))
            val location = remember { mutableStateOf("") }
            OutlinedTextField(value = location.value, onValueChange = {
                location.value = it
            }, placeholder = { TextContent(stringResource(R.string.Enter_city_name)) })
            AnimatedButton(modifier = Modifier.fillMaxWidth(), onClick = {
                onSaveFilterParam(
                    ParamsFilter(
                        country = when (val selectedFirst = selectedOptionCountry.value.first) {
                            WITHOUT -> null; else -> {
                                when (selectedFirst) {
                                    RUSSIA -> "ru"
                                    US -> "us"
                                    else -> "ru"
                                }
                            }
                        },
                        language = when (val selectedSecond = selectedOptionCountry.value.second) {
                            WITHOUT -> null; else -> {
                                when (selectedSecond) {
                                    RUSSIAN -> "ru"
                                    ENGLISH -> "en"
                                    else -> "ru"
                                }
                            }
                        },
                        category = when (val selectedNone = selectedCategory.value.second) {
                            NONE -> null
                            else -> selectedNone
                        },
                        location = if (location.value.trim() == "") null else location.value
                    )
                )
            }, text = stringResource(R.string.Apply))
        }
    }
}




