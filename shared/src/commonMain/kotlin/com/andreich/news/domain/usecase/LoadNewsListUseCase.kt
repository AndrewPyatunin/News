package com.andreich.news.domain.usecase

import com.andreich.news.domain.model.News
import com.andreich.news.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow

class LoadNewsListUseCase(
    private val repository: NewsRepository
) {

    operator fun invoke(): Flow<List<News>> {
        return repository.getNews()
    }
}