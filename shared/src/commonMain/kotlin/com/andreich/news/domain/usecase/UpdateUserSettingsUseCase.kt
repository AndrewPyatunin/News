package com.andreich.news.domain.usecase

import com.andreich.news.domain.model.UserSettings
import com.andreich.news.domain.repository.SettingsRepository

class UpdateUserSettingsUseCase(
    private val repository: SettingsRepository
) {

    suspend operator fun invoke(userSettings: UserSettings) {
        return repository.updateSettings(userSettings)
    }
}