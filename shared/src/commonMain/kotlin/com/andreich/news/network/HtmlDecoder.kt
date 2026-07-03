package com.andreich.news.network

internal fun String.decodeNumericEntities(): String =
    replace(Regex("&#(\\d+);")) { match ->
        val codePoint = match.groupValues[1].toIntOrNull()
        codePoint?.toChar()?.toString() ?: match.value
    }