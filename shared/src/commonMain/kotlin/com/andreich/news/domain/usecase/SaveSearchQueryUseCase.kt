package com.andreich.news.domain.usecase

import com.andreich.news.domain.repository.SearchHistoryRepository

class SaveSearchQueryUseCase(
    private val repository: SearchHistoryRepository
) {

    suspend operator fun invoke(query: String) {
        return repository.saveSearch(query)
    }
}