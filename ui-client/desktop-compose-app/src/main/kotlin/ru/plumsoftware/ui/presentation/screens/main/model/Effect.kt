package ru.plumsoftware.ui.presentation.screens.main.model

sealed class Effect {
    data object OpenSettings : Effect()
    data object OpenSandbox : Effect()
    data object OpenBidding : Effect()
}