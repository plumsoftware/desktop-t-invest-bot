package ru.plumsoftware.navigation.auth.variant.model

sealed class Event {
    data object Register : Event()
    data object Auth : Event()
}