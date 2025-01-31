package ru.plumsoftware.core.brokerage.model

data class TradingModel(
    val figi: String,
    val countLots: Int,
    val increase: Float,
    val decrease: Float,
)
