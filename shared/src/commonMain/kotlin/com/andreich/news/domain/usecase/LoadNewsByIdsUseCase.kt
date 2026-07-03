package com.andreich.news.domain.usecase

import com.andreich.news.domain.model.News
import com.andreich.news.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow

class LoadNewsByIdsUseCase(
    private val repository: NewsRepository
) {

    operator fun invoke(ids: List<Int>): Flow<List<News>> {
        return repository.getNewsListByIds(ids)
    }
}