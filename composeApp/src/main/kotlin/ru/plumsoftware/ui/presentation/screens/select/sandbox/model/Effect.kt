package ru.plumsoftware.ui.presentation.screens.select.sandbox.model

sealed class Effect {
    data object OpenSandbox : Effect()
    data object Back : Effect()
}