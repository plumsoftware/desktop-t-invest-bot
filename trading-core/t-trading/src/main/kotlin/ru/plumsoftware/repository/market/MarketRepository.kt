package ru.plumsoftware.repository.market

import ru.plumsoftware.net.core.model.dto.trading.TradingModelsDto
import ru.tinkoff.piapi.contract.v1.Instrument
import ru.tinkoff.piapi.contract.v1.InstrumentShort
import ru.tinkoff.piapi.core.models.Portfolio

interface MarketRepository {

    fun init(token: String)
    suspend fun getPortfolio(): Portfolio
    suspend fun getInstrumentByFigi(figi: String): Instrument
    suspend fun getInstrumentsBy(id: String): List<InstrumentShort>
    fun runLimitOrderTrading(tradingModelsDto: TradingModelsDto)
    fun stopLimitOrderTrading()
    fun closeApi()

}