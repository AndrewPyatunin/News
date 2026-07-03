package com.andreich.news.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.andreich.news.domain.model.Country
import com.andreich.news.domain.model.Language
import com.andreich.news.domain.model.UserSettings
import com.andreich.news.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl(
    val dataStore: DataStore<Preferences>,
    val deviceInfo: UserSettings
) : SettingsRepository {

    companion object {
        val COUNTRY = stringPreferencesKey("country")
        val LANGUAGE = stringPreferencesKey("language")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
    }

    override val prefs: Flow<UserSettings>
        get() = dataStore.data.map { prefs ->
            UserSettings(
                country = Country.valueOf(prefs[COUNTRY] ?: deviceInfo.country.name),
                language = Language.valueOf(prefs[LANGUAGE] ?: deviceInfo.language.name),
                darkTheme = prefs[DARK_THEME] ?: deviceInfo.darkTheme
            )
        }

    override suspend fun updateSettings(userSettings: UserSettings) {
        dataStore.edit { prefs ->
            prefs[COUNTRY] = userSettings.country.name.uppercase()
        }
        dataStore.edit { prefs ->
            prefs[LANGUAGE] = userSettings.language.name.uppercase()
        }
        dataStore.edit {
            it[DARK_THEME] = userSettings.darkTheme
        }
    }
}