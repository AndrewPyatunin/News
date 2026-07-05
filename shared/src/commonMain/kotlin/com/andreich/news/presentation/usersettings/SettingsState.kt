package com.andreich.news.presentation.usersettings

import com.andreich.news.domain.model.UserSettings
import com.andreich.news.presentation.core.UiState

data class SettingsState(
    val userSettings: UserSettings? = null,
    val isLoading: Boolean = false
) : UiState