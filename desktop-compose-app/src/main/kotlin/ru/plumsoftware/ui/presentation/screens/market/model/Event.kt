package ru.plumsoftware.ui.presentation.screens.market.model

sealed class Event {
    data object Back : Event()
    data object Init : Event()

    data class AddToTrading(
        val figi: String,
        val countLots: Int,
        val increase: String,
        val decrease: String,
        val isTrading: Boolean
    ) : Event()

    data class StartTrading(val isStartTrading: Boolean) : Event()
}