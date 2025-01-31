package ru.plumsoftware.mappers.trading

import ru.plumsoftware.net.core.model.dto.trading.TradingModelDto
import ru.plumsoftware.net.core.model.dto.trading.TradingModelsDto
import ru.plumsoftware.net.core.model.receive.trading.TradingModelReceive
import ru.plumsoftware.net.core.model.receive.trading.TradingModelsReceive

fun TradingModelDto.toReceive(): TradingModelReceive {
    return TradingModelReceive(
        figi = this.figi,
        lots = this.lots,
        tick = this.tick,
        startsWith = this.startsWith,
        tradingMode = this.tradingMode,
        percentIncrease = this.percentIncrease,
        percentDecrease = this.percentDecrease
    )
}

fun TradingModelsDto.toReceive2() : TradingModelsReceive {
    return TradingModelsReceive(
        id = this.id,
        tradingModelsReceive = this.tradingModelsDto.map {
            TradingModelReceive(
                figi = it.figi,
                lots = it.lots,
                tick = it.tick,
                startsWith = it.startsWith,
                tradingMode = it.tradingMode,
                percentIncrease = it.percentIncrease,
                percentDecrease = it.percentDecrease
            )
        }
    )
}