package ru.plumsoftware.ui.presentation.screens.market.model

sealed class Effect {
    data object Back : Effect()
    data class ShowSnackbar(val msg: String) : Effect()
}