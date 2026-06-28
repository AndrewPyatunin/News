package com.andreich.news.database

expect class NewsDatabaseFactory {

    fun create(): NewsDatabase
}