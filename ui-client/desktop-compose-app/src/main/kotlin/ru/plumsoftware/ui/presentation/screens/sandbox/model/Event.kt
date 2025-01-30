package ru.plumsoftware.ui.presentation.screens.sandbox.model

sealed class Event {
    data object Back : Event()
    data object Init : Event()
    data object CloseAllSandboxAccounts : Event()

    data class ChangeMoneyValue(val moneyValue: String) : Event()
    data object AddMoney : Event()

    data class SearchInstrument(val id: String) : Event()

    data class SelectInstrument(val figi: String) : Event()

    data class BuyWithMoney(val money: String) : Event()
    data class SellWithMoney(val money: String) : Event()

    data class BuyLot(val lot: String) : Event()
    data class SellLot(val lot: String) : Event()

    data class AddToTrading(
        val figi: String,
        val countLots: Int,
        val increase: String,
        val decrease: String,
        val isTrading: Boolean
    ) : Event()

    data class StartTrading(val isStartTrading: Boolean) : Event()
}