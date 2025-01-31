package ru.plumsoftware.core.settings.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    @SerialName("is_dark") val isDark: Boolean = false,
    @SerialName("api_tokens") val apiTokens: ApiTokens = ApiTokens()
)