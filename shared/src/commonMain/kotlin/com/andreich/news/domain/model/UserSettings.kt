package com.andreich.news.domain.model

data class UserSettings(
    val country: Country = Country.US,
    val language: Language = Language.EN,
    val darkTheme: Boolean = false
)


enum class Country {
    RU, US
}

enum class Language {
    RU, EN
}
