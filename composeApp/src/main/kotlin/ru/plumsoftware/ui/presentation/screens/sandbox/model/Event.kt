package ru.plumsoftware.ui.presentation.screens.sandbox.model

sealed class Event {
    data object Back : Event()
    data object Init : Event()
    data object CloseAllSandboxAccounts : Event()

    data class ChangeMoneyValue(val moneyValue: String) : Event()
    data object AddMoney : Event()

    data class SearchInstrument(val id: String) : Event()
}