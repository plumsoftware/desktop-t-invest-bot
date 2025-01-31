package ru.plumsoftware.navigation.confirm.number.model

sealed class Action {
    data object SendCode : Action()
    data class SetupPhoneNumber(val phone: String) : Action()
}