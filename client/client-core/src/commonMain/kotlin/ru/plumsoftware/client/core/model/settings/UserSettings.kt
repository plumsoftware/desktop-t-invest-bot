package ru.plumsoftware.client.core.model.settings

import kotlinx.serialization.Serializable

@Serializable
data class UserSettings(
    val id: Long,
    val isDarkTheme: Boolean
) {
    companion object {
        fun empty() = UserSettings(id = -1L, isDarkTheme = false)
    }
}
