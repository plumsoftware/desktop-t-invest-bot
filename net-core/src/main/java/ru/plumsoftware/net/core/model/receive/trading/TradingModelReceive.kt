package ru.plumsoftware.net.core.model.receive.trading

import kotlinx.serialization.Serializable
import ru.plumsoftware.net.core.model.mode.StartsWithMode
import ru.plumsoftware.net.core.model.mode.TradingMode

@Serializable
data class TradingModelReceive(
    val figi: String,
    val lots: Int,
    val startsWith: StartsWithMode,
    val tradingMode: TradingMode,
    val percentIncrease: Float,
    val percentDecrease: Float,
) {
    companion object {
        fun empty() : TradingModelReceive = TradingModelReceive(
            figi = "",
            lots = -1,
            startsWith = StartsWithMode.BUY,
            tradingMode = TradingMode.MARKET,
            percentDecrease = 0.0f,
            percentIncrease = 0.0f
        )
    }
}
