package com.andreich.news.data.mapper

import com.andreich.news.data.datasource.CityLookup
import com.andreich.news.data.datasource.SearchParamsDto
import com.andreich.news.domain.model.ParamsFilter

fun ParamsFilter.toSearchParamsDto(
    cityLookup: CityLookup
): SearchParamsDto {
    return SearchParamsDto(
        country = country,
        language = language,
        category = category,
        location = with(location) {
            this?.let {
                val city = cityLookup.findRuCity(this) ?: cityLookup.findCity(this)
                city
            }
        }?.let { city ->
            "${city.lat},${city.lng},10"
        }
    )
}