package ru.plumsoftware.navigation.confirm.number.model

sealed class Action {
    data object SendCode : Action()
}