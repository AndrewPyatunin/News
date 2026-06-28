package com.andreich.news.domain.usecase

import com.andreich.news.domain.repository.SearchHistoryRepository

class GetSuggestionsUseCase(
    private val repository: SearchHistoryRepository
) {

    operator fun invoke() = repository.suggestions
}