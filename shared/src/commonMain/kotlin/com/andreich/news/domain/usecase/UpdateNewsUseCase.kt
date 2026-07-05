package com.andreich.news.domain.usecase

import com.andreich.news.domain.model.RequestResult
import com.andreich.news.domain.repository.NewsRepository

class UpdateNewsUseCase(
    private val repository: NewsRepository
) {

    suspend operator fun invoke(language: String, country: String): RequestResult {
        return repository.updateNews(language, country)
    }
}