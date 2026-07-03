package com.andreich.news.domain.usecase

import com.andreich.news.domain.model.UserSettings
import com.andreich.news.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class GetUserSettingsUseCase(
    private val repository: SettingsRepository
) {

    operator fun invoke(): Flow<UserSettings> {
        return repository.prefs
    }
}