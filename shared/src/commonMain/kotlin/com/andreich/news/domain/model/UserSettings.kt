package com.andreich.news.domain.model

data class UserSettings(
    val country: Country,
    val language: Language,
    val darkTheme: Boolean = false
)


enum class Country(val country: String) {
    RUSSIA("ru"), USA("us")
}

enum class Language(val language: String) {
    RUSSIAN("ru"), ENGLISH("en")
}
