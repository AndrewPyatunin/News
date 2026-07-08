package com.andreich.news

import android.util.Log

actual class NewsLogger {
    actual fun log(tag: String, message: String) {
        Log.d(tag, message)
    }
}