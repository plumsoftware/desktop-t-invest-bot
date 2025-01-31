package ru.plumsoftware.repository.market

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import ru.plumsoftware.extensions.runTrading
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
    private val map = mutableMapOf<Long, TradingModelsDto>()


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

    override suspend fun runLimitOrderTrading(tradingModelsDto: TradingModelsDto) {
        if (!map.containsKey(tradingModelsDto.id)) {
            runTrading(tradingModelsDto = tradingModelsDto, tradingScope = tradingScope)
            map[tradingModelsDto.id] = tradingModelsDto
        } else throw Exception("Trading is already running.")
    }

    override fun getTradingStatus(id: Long): Boolean {
        return map.containsKey(id)
    }

    override fun stopLimitOrderTrading(id: Long) {
        if (map.containsKey(id)) {
            tradingScope.cancel()
            map.remove(id)
        } else throw Exception("Trading is not running.")
    }

    override fun closeApi() {
        investApi.destroy(3)
    }
}