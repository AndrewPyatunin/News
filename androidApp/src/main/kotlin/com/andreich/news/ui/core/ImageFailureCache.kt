package com.andreich.news.ui.core

object ImageFailureCache {
    private const val BLOCK_TIME_MS = 5 * 60 * 1000L

    private val failed = mutableMapOf<String, Long>()

    fun isBlocked(url: String): Boolean {
        val time = failed[url] ?: return false
        return System.currentTimeMillis() - time < BLOCK_TIME_MS
    }

    fun markFailed(url: String) {
        failed[url] = System.currentTimeMillis()
    }

    fun markSuccess(url: String) {
        failed.remove(url)
    }
}