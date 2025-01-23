package ru.plumsoftware.net.core.model.receive.trading

import kotlinx.serialization.Serializable

@Serializable
data class TradingModelsReceive(
    val id: Long,
    val tradingModelsReceive: List<TradingModelReceive>
) {
    companion object {
        fun empty(): TradingModelsReceive = TradingModelsReceive(
            id = -1L,
            tradingModelsReceive = emptyList()
        )
    }
}
