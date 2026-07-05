package com.andreich.news.presentation.usersettings

import com.andreich.news.presentation.core.UiIntent

sealed interface SettingsIntent : UiIntent {

    object LoadUserSettings : SettingsIntent
}