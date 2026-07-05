package com.andreich.news.di

import com.andreich.news.domain.usecase.AddToFavouritesUseCase
import com.andreich.news.domain.usecase.GetSuggestionsUseCase
import com.andreich.news.domain.usecase.GetUserSettingsUseCase
import com.andreich.news.domain.usecase.LoadFavoritesNewsUseCase
import com.andreich.news.domain.usecase.LoadNewsByIdsUseCase
import com.andreich.news.domain.usecase.LoadNewsListUseCase
import com.andreich.news.domain.usecase.LoadSingleNewsUseCase
import com.andreich.news.domain.usecase.ObserveCitiesUseCase
import com.andreich.news.domain.usecase.RemoveFromFavouritesUseCase
import com.andreich.news.domain.usecase.SaveSearchQueryUseCase
import com.andreich.news.domain.usecase.SearchNewsUseCase
import com.andreich.news.domain.usecase.UpdateNewsUseCase
import com.andreich.news.domain.usecase.UpdateSearchNewsUseCase
import com.andreich.news.domain.usecase.UpdateUserSettingsUseCase
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
    factory {
        GetUserSettingsUseCase(get())
    }
    factory {
        UpdateUserSettingsUseCase(get())
    }
    factory {
        UpdateNewsUseCase(get())
    }
    factory {
        UpdateSearchNewsUseCase(get())
    }
}