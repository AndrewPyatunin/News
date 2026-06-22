package com.andreich.news

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform