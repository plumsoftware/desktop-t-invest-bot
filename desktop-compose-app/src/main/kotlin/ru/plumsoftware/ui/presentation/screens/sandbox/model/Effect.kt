package ru.plumsoftware.ui.presentation.screens.sandbox.model

sealed class Effect {
    data object Back : Effect()
    data class ShowSnackbar(val msg: String) : Effect()
}