package ru.plumsoftware.repository.market

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import ru.plumsoftware.net.core.model.dto.trading.TradingModelDto
import ru.plumsoftware.net.core.model.dto.trading.TradingModelsDto
import ru.tinkoff.piapi.contract.v1.Account
import ru.tinkoff.piapi.contract.v1.Instrument
import ru.tinkoff.piapi.contract.v1.InstrumentShort
import ru.tinkoff.piapi.core.InvestApi
import ru.tinkoff.piapi.core.models.Portfolio

class MarketRepositoryImpl : MarketRepository {

    private lateinit var investApi: InvestApi
    private lateinit var account: Account

    private val tradingContext =
        Dispatchers.IO + SupervisorJob() + CoroutineName("Trading coroutine")
    private val tradingScope = CoroutineScope(tradingContext)


    override fun init(token: String) {
        investApi = InvestApi.create(token)
        account = investApi.userService.accountsSync[0]
    }

    override suspend fun getPortfolio(): Portfolio {
        return investApi.operationsService.getPortfolioSync(account.id)
    }

    override suspend fun getInstrumentByFigi(figi: String): Instrument {
        return investApi.instrumentsService.getInstrumentByFigiSync(figi)
    }

    override suspend fun getInstrumentsBy(id: String): List<InstrumentShort> {
        return investApi.instrumentsService.findInstrumentSync(id)
    }

    override fun runLimitOrderTrading(tradingModelsDto: TradingModelsDto) {
        val id = tradingModelsDto.id
        val tradingModels = tradingModelsDto.tradingModelsDto
        val jobs = mutableListOf<Job>()

        tradingModels.forEach { tradingModelDto: TradingModelDto ->
            val job = tradingScope.launch {
                //Trading
            }
            jobs.add(job)
        }
    }

    override fun stopLimitOrderTrading() {
        tradingScope.cancel()
    }

    override fun closeApi() {
        investApi.destroy(3)
    }
}