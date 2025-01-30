package ru.plumsoftware.navigation.auth.auth.model

sealed class Event {
    data object Next : Event()
    data object Back : Event()
    data object PrivacyPolicy : Event()
    data class OnPhoneChange(val phone: String) : Event()
    data class OnPasswordChange(val password: String) : Event()
}