package com.andreich.news.domain.usecase

import com.andreich.news.domain.model.News
import com.andreich.news.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow

class LoadSingleNewsUseCase(
    private val repository: NewsRepository
) {

    operator fun invoke(id: Int): Flow<News> {
        return repository.getSingleNews(id)
    }
}