package ru.plumsoftware.navigation.auth.variant.model

sealed class Effect {
    data object Auth : Effect()
    data object Register : Effect()
}