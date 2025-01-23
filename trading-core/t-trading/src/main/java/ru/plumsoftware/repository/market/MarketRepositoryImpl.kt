package ru.plumsoftware.repository.market

import ru.tinkoff.piapi.contract.v1.Account
import ru.tinkoff.piapi.contract.v1.Instrument
import ru.tinkoff.piapi.contract.v1.InstrumentShort
import ru.tinkoff.piapi.core.InvestApi
import ru.tinkoff.piapi.core.models.Portfolio

class MarketRepositoryImpl(token: String) : MarketRepository {

    private val investApi: InvestApi = InvestApi.create(token)
    private val account: Account = investApi.userService.accountsSync[0]

    override suspend fun getPortfolio(): Portfolio {
        return investApi.operationsService.getPortfolioSync(account.id)
    }

    override suspend fun getInstrumentByFigi(figi: String): Instrument {
        return investApi.instrumentsService.getInstrumentByFigiSync(figi)
    }

    override suspend fun getInstrumentsBy(id: String): List<InstrumentShort> {
        return investApi.instrumentsService.findInstrumentSync(id)
    }
}