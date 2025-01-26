package ru.plumsoftware.net.core.model.response.trading.market

import kotlinx.serialization.Serializable

@Serializable
data class PortfolioResponse(
    val totalPortfolio: MoneyResponse,
    val positions: List<PositionResponse>
)

@Serializable
data class MoneyResponse(
    val value: String,
    val currency: String
) {
    companion object {
        fun empty() = MoneyResponse(value = "", currency = "")
    }
}

@Serializable
data class PositionResponse(
    val figi: String,
    val instrumentUid: String,
    val currentPrice: MoneyResponse,
    val quantity: Long,
    val instrumentType: String,
    val varMargin: MoneyResponse
)