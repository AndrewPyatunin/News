package com.andreich.news.ext

import com.andreich.news.domain.model.CityItem
import org.maplibre.geojson.Feature
import org.maplibre.geojson.Point

 const val KEY_NAME = "name"
 const val KEY_NEWS_COUNT = "newsCount"

 const val KEY_NEWS_IDS = "newsIds"

fun CityItem.toFeature(): Feature {
    return Feature.fromGeometry(
        Point.fromLngLat(lng, lat)
    ).also {
        it.addStringProperty(KEY_NAME, name)
        it.addNumberProperty(KEY_NEWS_COUNT, news.size)
        val builder = StringBuilder("")
        for (news in news) builder.append(".${news.id}")
        val ids = builder.toString()
        it.addStringProperty(KEY_NEWS_IDS, ids)
    }
}