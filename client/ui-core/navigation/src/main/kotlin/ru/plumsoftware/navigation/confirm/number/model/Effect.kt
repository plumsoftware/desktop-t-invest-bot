package ru.plumsoftware.navigation.confirm.number.model

sealed class Effect {
    data object Next : Effect()
    data object Back : Effect()
}