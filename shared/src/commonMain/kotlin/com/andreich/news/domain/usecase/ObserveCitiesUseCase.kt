package com.andreich.news.domain.usecase

import com.andreich.news.domain.model.CityItem
import com.andreich.news.domain.repository.CityRepository
import kotlinx.coroutines.flow.Flow

class ObserveCitiesUseCase(
    private val repository: CityRepository
) {

    operator fun invoke(isEnglish: Boolean): Flow<List<CityItem>> {
        return repository.observeCities(isEnglish)
    }
}