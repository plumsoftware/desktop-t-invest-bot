package ru.plumsoftware.facade

import ru.plumsoftware.mappers.trading.instrumentToResponse
import ru.plumsoftware.net.core.model.response.trading.market.InstrumentResponse
import ru.plumsoftware.repository.market.MarketRepository
import ru.plumsoftware.repository.sandbox.SandboxRepository

class TTradingFacade(
    private val marketRepository: MarketRepository,
    private val sandboxRepository: SandboxRepository
) {

    suspend fun getMarketInstrumentBy(id: String): List<InstrumentResponse> {
        return marketRepository.getInstrumentsBy(id).instrumentToResponse()
    }

}