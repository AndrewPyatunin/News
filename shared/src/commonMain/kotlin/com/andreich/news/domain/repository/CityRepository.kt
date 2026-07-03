package com.andreich.news.domain.repository

import com.andreich.news.domain.model.CityItem
import kotlinx.coroutines.flow.Flow

interface CityRepository {

    fun observeCities(isEnglish: Boolean): Flow<List<CityItem>>
}