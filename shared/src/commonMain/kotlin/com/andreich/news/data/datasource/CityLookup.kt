package com.andreich.news.data.datasource


expect class CityLookup {

    fun findCityList(param: String, isEnglish: Boolean? = null): List<CityDto>

    fun findCity(param: String): CityDto?

    fun findRuCity(word: String): CityDto?
}