package ru.plumsoftware.navigation.auth.privacy.policy.model

sealed class Event {
    data object Read : Event()
}