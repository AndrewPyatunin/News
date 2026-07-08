package com.andreich.news

expect class NewsLogger() {

    fun log(tag: String, message: String)
}