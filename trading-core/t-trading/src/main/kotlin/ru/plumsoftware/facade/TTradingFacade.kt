package ru.plumsoftware.facade

import ru.plumsoftware.mappers.trading.instrumentToResponse
import ru.plumsoftware.mappers.trading.toDto
import ru.plumsoftware.net.core.model.receive.TTokensReceive
import ru.plumsoftware.net.core.model.receive.trading.TradingModelsReceive
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

    fun runMarketLimitOrderTrading(tradingModelsReceive: TradingModelsReceive) {
        marketRepository.runLimitOrderTrading(tradingModelsDto = tradingModelsReceive.toDto())
    }

    fun destroy() {
        marketRepository.closeApi()
        sandboxRepository.closeApi()
    }
}