package com.andreich.news.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andreich.news.domain.model.UserSettings
import com.andreich.news.domain.usecase.GetUserSettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val getUserSettingsUseCase: GetUserSettingsUseCase
) : ViewModel() {

    private val _errors = MutableStateFlow("")
    val errors: StateFlow<String> = _errors.asStateFlow()
    private val _settings = MutableStateFlow(UserSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    init {
        viewModelScope.launch {
            getUserSettingsUseCase().onEach { userSettings ->
                _settings.update {
                    userSettings
                }
            }.catch { throwable ->
                _errors.update {
                    throwable.message.orEmpty()
                }
            }.collect()
        }

}
}