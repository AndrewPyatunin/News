package com.andreich.news.domain.usecase

import com.andreich.news.domain.model.ParamsFilter
import com.andreich.news.domain.model.RequestResult
import com.andreich.news.domain.repository.NewsRepository

class UpdateSearchNewsUseCase(
    private val repository: NewsRepository
) {

    suspend operator fun invoke(param: String, paramsFilter: ParamsFilter?): RequestResult {
        return repository.updateSearchedNews(param, paramsFilter)
    }
}