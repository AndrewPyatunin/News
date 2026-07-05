package com.andreich.news.presentation.usersettings

import com.andreich.news.domain.usecase.GetUserSettingsUseCase
import com.andreich.news.presentation.core.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class SettingsViewModel(
    private val getUserSettingsUseCase: GetUserSettingsUseCase
) : BaseViewModel<SettingsState, SettingsEvent, SettingsIntent>(SettingsState()) {

    override fun sendIntent(intent: SettingsIntent) {
        launch {
            when (intent) {
                SettingsIntent.LoadUserSettings -> {
                    getUserSettingsUseCase().onEach { userSettings ->
                        _state.update { it.copy(userSettings = userSettings) }
                    }.collect()
                }
            }
        }

    }

    override suspend fun onError(e: Throwable) {
        _events.emit(SettingsEvent.ShowError(e.message.orEmpty()))
    }
}
