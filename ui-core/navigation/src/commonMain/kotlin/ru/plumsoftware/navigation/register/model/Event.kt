package ru.plumsoftware.navigation.register.model

sealed class Event {
    data object Back : Event()
    data object Next : Event()

    data class OnNameChanged(val name: String) : Event()
    data class OnPhoneChanged(val phone: String) : Event()
    data class OnPasswordChanged(val password: String) : Event()

    data object PrivacyPolicy : Event()
}