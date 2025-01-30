package ru.plumsoftware.navigation.auth.auth.model

sealed class Effect {
    data object Back : Effect()
    data object Next : Effect()
    data object PrivacyPolicy : Effect()
}