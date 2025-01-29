package ru.plumsoftware.navigation.confirm.number.model

sealed class Event {
    data object Back : Event()
    data class OnCodeChanged(val code: String) : Event()
    data object Confirm : Event()
}