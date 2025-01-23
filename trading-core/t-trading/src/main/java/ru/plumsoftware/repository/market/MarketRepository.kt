package ru.plumsoftware.repository.market

import ru.tinkoff.piapi.contract.v1.Instrument
import ru.tinkoff.piapi.core.models.Portfolio

interface MarketRepository {

    suspend fun getPortfolio() : Portfolio
    suspend fun getInstrumentByFigi(figi: String) : Instrument

}