package com.andreich.news

import platform.Foundation.NSLog

actual class NewsLogger {
    actual fun log(tag: String, message: String) {
        NSLog(message)
    }
}