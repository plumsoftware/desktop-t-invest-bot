package ru.plumsoftware.mappers.trading

import ru.plumsoftware.net.core.model.dto.TTokensDto
import ru.plumsoftware.net.core.model.dto.trading.TradingModelDto
import ru.plumsoftware.net.core.model.dto.trading.TradingModelsDto
import ru.plumsoftware.net.core.model.receive.TTokensReceive
import ru.plumsoftware.net.core.model.receive.trading.TradingModelReceive
import ru.plumsoftware.net.core.model.receive.trading.TradingModelsReceive
import ru.plumsoftware.net.core.model.response.trading.market.InstrumentResponse
import ru.plumsoftware.net.core.model.response.trading.market.MoneyResponse
import ru.plumsoftware.net.core.model.response.trading.market.PortfolioResponse
import ru.plumsoftware.net.core.model.response.trading.market.PositionResponse
import ru.tinkoff.piapi.contract.v1.InstrumentShort
import ru.tinkoff.piapi.core.models.Portfolio
import java.util.UUID

fun Portfolio.portfolioToResponse(): PortfolioResponse {
    return PortfolioResponse(
        totalPortfolio = MoneyResponse(
            value = this.totalAmountPortfolio.value.toString(),
            currency = this.totalAmountPortfolio.currency
        ),
        positions = this.positions.map {
            PositionResponse(
                figi = it.figi,
                instrumentUid = it.instrumentUid,
                instrumentType = it.instrumentType,
                currentPrice = MoneyResponse(
                    value = it.currentPrice.value.toString(),
                    currency = it.currentPrice.currency
                ),
                quantity = it.quantity.toLong(),
                varMargin = MoneyResponse(
                    value = it.varMargin.value.toString(),
                    currency = it.varMargin.currency
                )
            )
        }
    )
}

fun List<InstrumentShort>.instrumentToResponse(): List<InstrumentResponse> {
    return this.map {
        InstrumentResponse(
            figi = it.figi,
            ticker = it.ticker,
            name = it.name,
            instrumentType = it.instrumentType
        )
    }
}

fun TradingModelsReceive.toDto(): TradingModelsDto {
    return TradingModelsDto(
        id = this.id,
        tradingModelsDto = this.tradingModelsReceive.map {
            TradingModelDto(
                tradingModelId = UUID.randomUUID().leastSignificantBits,
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

fun TTokensReceive.toDto(): TTokensDto {
    return TTokensDto(
        sandboxToken = this.sandboxToken,
        marketToken = this.marketToken
    )
}

fun TradingModelsDto.toReceive() : TradingModelsReceive {
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