package com.andreich.news.domain.model

data class UserSettings(
    val country: Country? = null,
    val language: Language? = null,
    val darkTheme: Boolean? = null
)


enum class Country {
    RU, US
}

enum class Language {
    RU, EN
}
