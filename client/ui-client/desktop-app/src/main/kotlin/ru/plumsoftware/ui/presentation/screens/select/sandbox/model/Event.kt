package ru.plumsoftware.ui.presentation.screens.select.sandbox.model

import ru.tinkoff.piapi.contract.v1.Account

sealed class Event {
    data object Init : Event()
    data object OpenNew : Event()
    data object Close : Event()
    data class CloseAccount(val account: Account) : Event()
    data class SelectAccount(val account: Account) : Event()
}