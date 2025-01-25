package ru.plumsoftware.facade

import ru.plumsoftware.mappers.trading.instrumentToResponse
import ru.plumsoftware.mappers.trading.toDto
import ru.plumsoftware.model.TradingMode
import ru.plumsoftware.net.core.model.receive.TTokensReceive
import ru.plumsoftware.net.core.model.receive.trading.TradingModelsReceive
import ru.plumsoftware.net.core.model.response.trading.TradingStatus
import ru.plumsoftware.net.core.model.response.trading.market.InstrumentResponse
import ru.plumsoftware.repository.market.MarketRepository
import ru.plumsoftware.repository.sandbox.SandboxRepository

class TTradingFacade(
    private val marketRepository: MarketRepository,
    private val sandboxRepository: SandboxRepository
) {

    fun init(tTokensReceive: TTokensReceive) {
        marketRepository.init(token = tTokensReceive.marketToken)
        sandboxRepository.init(sandboxToken = tTokensReceive.sandboxToken)
    }

    suspend fun getMarketInstrumentBy(id: String): List<InstrumentResponse> {
        return marketRepository.getInstrumentsBy(id).instrumentToResponse()
    }

    suspend fun runMarketLimitOrderTrading(tradingModelsReceive: TradingModelsReceive) {
        marketRepository.runLimitOrderTrading(tradingModelsDto = tradingModelsReceive.toDto())
    }

    suspend fun runSandboxTrading(tradingModelsReceive: TradingModelsReceive) {
        sandboxRepository.runLimitOrderTrading(tradingModelsDto = tradingModelsReceive.toDto())
    }

    fun getTradingStatus(id: Long, mode: TradingMode): TradingStatus {
        return when (mode) {
            TradingMode.MARKET -> {
                val res = marketRepository.getTradingStatus(id = id)
                if (res) TradingStatus.RUN
                else TradingStatus.STOP
            }

            TradingMode.SANDBOX -> {
                val res = sandboxRepository.getTradingStatus(id = id)
                if (res) TradingStatus.RUN
                else TradingStatus.STOP
            }
        }
    }

    fun stopTrading(id: Long, mode: TradingMode) {
        when (mode) {
            TradingMode.MARKET -> {
                marketRepository.stopLimitOrderTrading(id = id)
            }

            TradingMode.SANDBOX -> {
                sandboxRepository.stopLimitOrderTrading(id = id)
            }
        }
    }

    fun destroy() {
        marketRepository.closeApi()
        sandboxRepository.closeApi()
    }
}