package com.andreich.news.data.repository

import com.andreich.news.data.datasource.CityLookup
import com.andreich.news.data.mapper.toDomain
import com.andreich.news.database.NewsDao
import com.andreich.news.domain.model.CityItem
import com.andreich.news.domain.model.News
import com.andreich.news.domain.repository.CityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CityRepositoryImpl(
    val cityLookup: CityLookup,
    val newsDao: NewsDao
) : CityRepository {
    override fun observeCities(isEnglish: Boolean): Flow<List<CityItem>> {
        return newsDao.getNewsFlow().map { list ->
            list.map {
                it.toDomain()
            }.buildCityItems(isEnglish = isEnglish)
        }
    }

    private fun List<News>.buildCityItems(isEnglish: Boolean = true): List<CityItem> {
        return flatMap { news ->
            cityLookup.findCityList(
                "${news.title} ${news.description} ${news.content}",
                isEnglish
            ).map {
                it to news
            }
        }.groupBy(keySelector = { it.first }, valueTransform = { it.second }).map { (city, news) ->
            CityItem(
                id = city.id,
                lat = city.lat,
                lng = city.lng,
                name = city.name,
                news = news
            )
        }
    }
}