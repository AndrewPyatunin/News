package com.andreich.news.data.datasource

actual class CityLookup {

    actual fun findCityList(param: String, isEnglish: Boolean?): List<CityDto> {
        return emptyList()
    }

    actual fun findCity(param: String): CityDto? = null

    actual fun findRuCity(word: String): CityDto? = null
}