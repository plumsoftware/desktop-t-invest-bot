package ru.plumsoftware.ui.presentation.screens.sandbox.model

sealed class Event {
    data object Back : Event()
    data object Init : Event()
    data object CloseAllSandboxAccounts : Event()
}