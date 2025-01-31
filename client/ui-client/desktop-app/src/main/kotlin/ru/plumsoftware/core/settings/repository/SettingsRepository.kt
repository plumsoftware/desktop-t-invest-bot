package ru.plumsoftware.core.settings.repository

import ru.plumsoftware.core.settings.model.ApiTokens
import ru.plumsoftware.core.settings.model.Settings

interface SettingsRepository {
    suspend fun saveTokens(apiTokens: ApiTokens)
    suspend fun saveIsDark(isDark: Boolean)
    suspend fun saveSettings(settings: Settings)
    suspend fun getSettings(): Settings
}