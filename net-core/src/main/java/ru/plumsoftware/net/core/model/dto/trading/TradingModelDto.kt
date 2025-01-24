package ru.plumsoftware.net.core.model.dto.trading

import ru.plumsoftware.net.core.model.mode.StartsWithMode
import ru.plumsoftware.net.core.model.mode.TradingMode

data class TradingModelDto(
    val tradingModelId: Long,
    val figi: String,
    val lots: Int,
    val tick: Long,
    val startsWith: StartsWithMode,
    val tradingMode: TradingMode,
    val percentIncrease: Float,
    val percentDecrease: Float,
) {
    companion object {
        fun empty() : TradingModelDto = TradingModelDto(
            tradingModelId = -1L,
            figi = "",
            lots = -1,
            tick = -1L,
            startsWith = StartsWithMode.BUY,
            tradingMode = TradingMode.MARKET,
            percentDecrease = 0.0f,
            percentIncrease = 0.0f
        )
    }
}