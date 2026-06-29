package com.andreich.news.di

import com.andreich.news.presentation.newsdetail.NewsDetailsViewModel
import com.andreich.news.presentation.newsfavorite.NewsFavoriteViewModel
import com.andreich.news.presentation.newslist.NewsListViewModel
import com.andreich.news.presentation.newssearch.NewsSearchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel {
        NewsListViewModel(get())
    }
    viewModel {
        NewsDetailsViewModel(get(), get())
    }
    viewModel {
        NewsSearchViewModel(get(), get(), get())
    }
    viewModel {
        NewsFavoriteViewModel(get(), get(), get())
    }
}