package ru.plumsoftware.navigation.auth.auth.model

sealed class Event {
    data object Next : Event()
    data object Back : Event()
    data class OnPhoneChange(val phone: String) : Event()
}