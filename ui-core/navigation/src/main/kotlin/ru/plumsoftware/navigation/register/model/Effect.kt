package ru.plumsoftware.navigation.register.model

sealed class Effect {
    data object Back : Effect()
    data object Next : Effect()
}