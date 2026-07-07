package com.andreich.news.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andreich.news.R
import com.andreich.news.domain.model.ParamsFilter
import com.andreich.news.ext.MenuPopUpItem
import com.andreich.news.ext.NewsItem
import com.andreich.news.ext.TextContent
import com.andreich.news.ext.TextHeader
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
        if (state.popUpMenuShowed) {
            NewsPopUp(onSaveFilterParam = {
                onIntent(SaveFilterParams(it))
            }, onDismiss = {
                onIntent(FilterMenuClick)
            })
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
}

@Composable
fun NewsSearchRoute(snackbarState: SnackbarHostState, onNavigateToNewsDetails: (Int) -> Unit) {
    val viewModel: NewsSearchViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect {
            when (it) {
                is NewsSearchEvent.ShowError -> snackbarState.showSnackbar(message = it.message)
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
fun NewsPopUp(onSaveFilterParam: (ParamsFilter) -> Unit, onDismiss: () -> Unit) {
    val scrollableState = rememberScrollState()

    val RUSSIA = stringResource(R.string.russia)
    val RUSSIAN = stringResource(R.string.russian)
    val ENGLISH = stringResource(R.string.english)
    val US = stringResource(R.string.us)
    val NONE = stringResource(R.string.without_param)
    val EMPTY = ""
    val listCountriesToLanguages: List<Pair<String, String>> = listOf(
        NONE to NONE,
        RUSSIA to RUSSIAN,
        US to ENGLISH,
    )
    MenuPopUpItem(alignment = Alignment.BottomCenter, onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    state = scrollableState
                )
        ) {
            val selectedOptionCountry = remember { mutableStateOf(NONE to NONE) }
            TextHeader("${stringResource(R.string.news_country)}\n(${stringResource(R.string.response_language)})")
            HorizontalDivider()
            RadioButtonSelection(radioOptions = listCountriesToLanguages) {
                selectedOptionCountry.value = it
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
            val (selectedOption, onOptionSelected) = remember {
                mutableStateOf(
                    localizedCategories[0]
                )
            }
            TextHeader(stringResource(R.string.category_search))
            Column(Modifier.selectableGroup()) {
                localizedCategories.forEachIndexed { index, text ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (text == selectedOption),
                                onClick = { onOptionSelected(text) },
                            ), verticalAlignment = Alignment.CenterVertically
                    )
                    {
                        RadioButton(
                            selected = (selectedOption == text),
                            onClick = { onOptionSelected(text) }
                        )
                        Text(text = text, fontSize = 16.sp)

                    }
                }
            }
            val selectedCategory = categories[localizedCategories.indexOf(selectedOption)]
            HorizontalDivider()
            TextHeader(stringResource(R.string.location_search))
            val location = remember { mutableStateOf("") }
            OutlinedTextField(value = location.value, onValueChange = {
                location.value = it
            }, placeholder = { TextContent(stringResource(R.string.Enter_city_name)) })
            AnimatedButton(modifier = Modifier.fillMaxWidth(), onClick = {
                onSaveFilterParam(
                    ParamsFilter(
                        country = when(val selectedFirst = selectedOptionCountry.value.first) { NONE -> null; else -> selectedFirst },
                        language = when (val selectedSecond = selectedOptionCountry.value.second) { NONE -> null; else -> selectedSecond },
                        category = if (selectedCategory == "none") null else selectedCategory,
                        location = if (location.value.trim() == "") null else location.value
                    )
                )
            }, text = stringResource(R.string.Apply))
        }
    }
}

@Composable
fun AnimatedButton(modifier: Modifier, onClick: () -> Unit, text: String) {
    val interactionSource = remember { MutableInteractionSource() }
    var isPressed = interactionSource.collectIsPressedAsState()
    val sizeScale by animateFloatAsState(if (isPressed.value) 0.8f else 1f)

    Button(
        onClick = onClick,
        modifier = modifier
            .indication(interactionSource, LocalIndication.current)
            .clickable { isPressed.value }
            .padding(16.dp)
            .wrapContentSize()
            .graphicsLayer(
                scaleX = sizeScale,
                scaleY = sizeScale
            ),
        shape = RoundedCornerShape(10),
        elevation = ButtonDefaults.buttonElevation(if (isPressed.value) 10.dp else 0.dp),
        interactionSource = interactionSource
    ) {
        TextHeader(text)
    }

}
    @Composable
    fun RadioButtonSelection(
        modifier: Modifier = Modifier,
        radioOptions: List<Pair<String, String>>,
        onParam: (Pair<String, String>) -> Unit
    ) {
        val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
        Column(modifier.selectableGroup()) {
            radioOptions.forEach { text ->
                Row(
                    modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .selectable(
                            selected = (text == selectedOption),
                            onClick = {
                                onOptionSelected(text)
                                onParam(selectedOption) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (text == selectedOption),
                        onClick = null
                    )
                    Text(
                        fontSize = 16.sp,
                        text = " ${stringResource(R.string.country)} ${text.first} (${text.second} ${stringResource(R.string.language)})",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
}



