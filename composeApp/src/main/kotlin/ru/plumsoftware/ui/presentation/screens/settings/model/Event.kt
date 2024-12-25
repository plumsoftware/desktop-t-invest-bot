package ru.plumsoftware.ui.presentation.screens.settings.model

sealed class Event {

    data object Back : Event()

    data class ChangeIsDark(val isDark: Boolean) : Event()
    data class ChangeToken(val token: String) : Event()
    data class ChangeSandboxToken(val sandboxToken: String) : Event()

    data object SaveSettings : Event()
}