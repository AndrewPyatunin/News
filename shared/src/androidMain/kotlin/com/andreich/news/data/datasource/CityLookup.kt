package com.andreich.news.data.datasource

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import news.shared.generated.resources.Res
import java.util.Locale.getDefault

actual class CityLookup(
    context: Context
) {
    private var citiesMain: Map<String, CityDto> = emptyMap()
    private var cities: Map<String, CityDto> = emptyMap()
    private var ruCities: Map<String, CityDto> = emptyMap()

    private val json = Json {ignoreUnknownKeys = true}
    private val cityRegexRu = Regex(
        """\b[\p{Lu}][\p{L}'-]+(?:\s+(?:[\p{Lu}][\p{L}'-]+|на|имени|де|эль|оф))*"""
    )

    private val cityRegexEn = Regex(
        """\b(?:[A-Z][\p{L}'-]+|St\.)\b(?:\s+(?:of|de|del|da|di|du|la|le|el|van|von|san|santa|saint|chi|minh)\b|\s+(?:[A-Z][\p{L}'-]+|St\.))*"""
    )

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val textFirst = Res.readBytes("files/town.json").decodeToString()
            val text = Res.readBytes("files/cities.json").decodeToString()
            val textRu = Res.readBytes("files/cities_ru.json").decodeToString()
            ruCities =
                json.decodeFromString<List<CityDto>>(textRu).associateBy { it.name.normalize() }
            citiesMain = json.decodeFromString<List<CityDto>>(textFirst).associateBy { it.name.normalize() }
            cities = json.decodeFromString<List<CityDto>>(text).associateBy { it.name.normalize() }
            (cities as MutableMap).putAll(citiesMain)
        }
    }

    actual fun findCityList(param: String, isEnglish: Boolean?): List<CityDto> {
        if (isEnglish == null) return findUniversalCity(param)
        isEnglish.let {
            val listWords = extractWord(param, it)
            return if (it) {
                listWords.mapNotNull {
                    findCity(it.normalize())
                }
            } else {
                listWords.mapNotNull {
                    findRuCity(it.normalize())
                }
            }
        }
    }
    private fun findUniversalCity(param: String): List<CityDto> {
        val rus = extractWord(param, false).mapNotNull {
            findRuCity(it.normalize())
        }
        val eng = extractWord(param, true).mapNotNull {
                    findCity(it.normalize())
                }

        return rus.toMutableList().apply {
            addAll(eng)
        }.toList()
    }

    actual fun findCity(param: String): CityDto? {
        return cities[param]
    }

    actual fun findRuCity(word: String): CityDto? {
        ruCities[word]?.let {
            return it
        } ?: ruCities[word.dropLast(1)]?.let {
            return it
        } ?: ruCities[word.dropLast(1).plus("а")]?.let {
            return it
        } ?: ruCities[word.dropLast(2)]?.let {
            return it
        } ?: ruCities[word.dropLast(2).plus("а")]?.let { return it } ?: return null
    }

    private fun String.normalize() = trim().lowercase(getDefault())

    private fun extractWord(text: String, isEnglish: Boolean): List<String> {
        return (if (isEnglish) cityRegexEn else cityRegexRu)
            .findAll(text)
            .map { it.value.trim() }
            .filter { it.count(Char::isLetter) >= 3 }
            .distinct()
            .toList()
    }
}