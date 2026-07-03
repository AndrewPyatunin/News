package com.andreich.news.domain.repository

import com.andreich.news.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    val prefs: Flow<UserSettings>

    suspend fun updateSettings(userSettings: UserSettings)
}