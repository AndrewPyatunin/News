package com.andreich.news.di

import com.andreich.news.domain.usecase.AddToFavouritesUseCase
import com.andreich.news.domain.usecase.GetSuggestionsUseCase
import com.andreich.news.domain.usecase.LoadFavoritesNewsUseCase
import com.andreich.news.domain.usecase.LoadNewsByIdsUseCase
import com.andreich.news.domain.usecase.LoadNewsListUseCase
import com.andreich.news.domain.usecase.LoadSingleNewsUseCase
import com.andreich.news.domain.usecase.ObserveCitiesUseCase
import com.andreich.news.domain.usecase.RemoveFromFavouritesUseCase
import com.andreich.news.domain.usecase.SaveSearchQueryUseCase
import com.andreich.news.domain.usecase.SearchNewsUseCase
import org.koin.dsl.module

val domainModule = module {
    factory {
        AddToFavouritesUseCase(get())
    }
    factory {
        GetSuggestionsUseCase(get())
    }
    factory {
        LoadFavoritesNewsUseCase(get())
    }
    factory {
        LoadNewsListUseCase(get())
    }
    factory {
        SaveSearchQueryUseCase(get())
    }
    factory {
        SearchNewsUseCase(get())
    }
    factory {
        RemoveFromFavouritesUseCase(get())
    }
    factory {
        LoadSingleNewsUseCase(get())
    }
    factory {
        ObserveCitiesUseCase(get())
    }
    factory {
        LoadNewsByIdsUseCase(get())
    }
}