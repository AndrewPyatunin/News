package com.andreich.news.domain.usecase

import com.andreich.news.domain.repository.NewsRepository

class AddToFavouritesUseCase(
    private val repository: NewsRepository
) {

    suspend operator fun invoke(newsId: Int) {
        return repository.addToFavourites(newsId)
    }
}