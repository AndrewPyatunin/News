package com.andreich.news.presentation.core

sealed interface UiMessage {

    class ShowError(val message: String): UiMessage

    class ShowSuccess(val message: String): UiMessage
}