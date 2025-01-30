package ru.plumsoftware.navigation.auth.privacy.policy.model

sealed class Effect {
    data object Read : Effect()
}