package ru.plumsoftware.repository.sandbox

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import ru.plumsoftware.extensions.runTrading
import ru.plumsoftware.net.core.model.dto.trading.TradingModelsDto
import ru.tinkoff.piapi.contract.v1.Account
import ru.tinkoff.piapi.contract.v1.InstrumentShort
import ru.tinkoff.piapi.contract.v1.MoneyValue
import ru.tinkoff.piapi.core.InvestApi
import ru.tinkoff.piapi.core.models.Portfolio
import ru.tinkoff.piapi.core.models.Positions

class SandboxRepositoryImpl : SandboxRepository {

    private lateinit var investApi: InvestApi

    private val tradingContext =
        Dispatchers.IO + SupervisorJob() + CoroutineName("Trading sandbox coroutine")
    private val tradingScope = CoroutineScope(tradingContext)
    private val map = mutableMapOf<Long, TradingModelsDto>()

    override fun init(sandboxToken: String) {
        investApi = InvestApi.createSandbox(sandboxToken)
    }

    override suspend fun getPortfolio(accountId: String): Portfolio {
        val portfolio = investApi.operationsService.getPortfolioSync(accountId)
        return portfolio
    }

    override suspend fun getPositions(accountId: String): Positions {
        val positions = investApi.operationsService.getPositionsSync(accountId)
        return positions
    }

    override suspend fun createNewSandbox(name: String): List<Account> {
        investApi.sandboxService.openAccountSync(name)
        val accounts = investApi.userService.accountsSync
        return accounts
    }

    override suspend fun closeAccount(accountId: String) {
        investApi.sandboxService.closeAccountSync(accountId)
    }

    override suspend fun getAccounts(): List<Account> {
        return investApi.userService.accountsSync
    }

    override suspend fun addMoney(value: Int, currency: String, accountId: String) {
        investApi.sandboxService.payIn(
            accountId,
            MoneyValue
                .newBuilder()
                .setUnits(value.toLong())
                .setCurrency(currency)
                .build()
        )
    }

    override suspend fun getInstrumentsBy(id: String): List<InstrumentShort> {
        return investApi.instrumentsService.findInstrumentSync(id)
    }

    override fun getTradingStatus(id: Long): Boolean {
        return map.containsKey(id)
    }

    override suspend fun runLimitOrderTrading(tradingModelsDto: TradingModelsDto) {
        if (!map.containsKey(tradingModelsDto.id)) {
            runTrading(tradingModelsDto = tradingModelsDto, tradingScope = tradingScope)
            map[tradingModelsDto.id] = tradingModelsDto
        } else throw Exception("Trading is already run.")
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