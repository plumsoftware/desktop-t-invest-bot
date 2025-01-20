package ru.plumsoftware.ui.presentation.screens.select.market.model

import ru.tinkoff.piapi.contract.v1.Account

sealed class Event {
    data object Init : Event()
    data object Close : Event()
    data class SelectAccount(val account: Account) : Event()
}