package ru.plumsoftware.ui.presentation.screens.main.model

sealed class Event {
    data object OpenSettingsClick : Event()
    data object OpenSandboxClick : Event()
    data object OpenBiddingClick : Event()
}