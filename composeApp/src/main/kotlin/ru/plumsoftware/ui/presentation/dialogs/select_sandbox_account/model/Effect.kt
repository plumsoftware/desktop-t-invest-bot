package ru.plumsoftware.ui.presentation.dialogs.select_sandbox_account.model

sealed class Effect {
    data object OpenSandbox : Effect()
    data object Back : Effect()
}