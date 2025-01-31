package ru.plumsoftware.mappers.trading

import ru.plumsoftware.net.core.model.dto.trading.TradingModelDto
import ru.plumsoftware.net.core.model.dto.trading.TradingModelsDto
import ru.plumsoftware.net.core.model.receive.trading.TradingModelReceive
import ru.plumsoftware.net.core.model.receive.trading.TradingModelsReceive
import java.util.UUID

fun TradingModelsReceive.toDto1(): TradingModelsDto {
    return TradingModelsDto(
        id = this.id,
        tradingModelsDto = this.tradingModelsReceive.map { it.toDto2() }
    )
}

fun TradingModelReceive.toDto2(): TradingModelDto {
    return TradingModelDto(
        tradingModelId = UUID.randomUUID().leastSignificantBits,
        figi = this.figi,
        lots = this.lots,
        tick = this.tick,
        startsWith = this.startsWith,
        tradingMode = this.tradingMode,
        percentIncrease = this.percentIncrease,
        percentDecrease = this.percentDecrease
    )
}