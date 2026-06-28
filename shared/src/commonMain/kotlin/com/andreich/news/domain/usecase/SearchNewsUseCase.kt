package com.andreich.news.domain.usecase

import com.andreich.news.domain.model.News
import com.andreich.news.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow

class SearchNewsUseCase(
    private val repository: NewsRepository
) {

    operator fun invoke(param: String): Flow<List<News>> {
        return repository.searchNews(param)
    }
}