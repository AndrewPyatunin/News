package com.andreich.news.domain.usecase

import com.andreich.news.domain.repository.NewsRepository

class GetNewsSuggestionsUseCase(
    private val repository: NewsRepository
) {

    suspend operator fun invoke(query: String): List<String> {
        return repository.getNewsSuggestions(query)
    }
}