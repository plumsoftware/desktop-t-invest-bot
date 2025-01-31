package ru.plumsoftware.ui.presentation.screens.select.market.model

sealed class Effect {
    data object Back : Effect()
    data object Next : Effect()
}