package ru.plumsoftware.repository.sandbox

import ru.plumsoftware.net.core.model.dto.trading.TradingModelsDto
import ru.tinkoff.piapi.contract.v1.Account
import ru.tinkoff.piapi.contract.v1.InstrumentShort
import ru.tinkoff.piapi.core.models.Portfolio
import ru.tinkoff.piapi.core.models.Positions

interface SandboxRepository {

    fun init(sandboxToken: String)
    suspend fun getPortfolio(accountId: String): Portfolio
    suspend fun getPositions(accountId: String): Positions
    suspend fun createNewSandbox(name: String): List<Account>
    suspend fun closeAccount(accountId: String)
    suspend fun getAccounts(): List<Account>
    suspend fun addMoney(value: Int, currency: String, accountId: String)
    suspend fun getInstrumentsBy(id: String): List<InstrumentShort>
    fun getTradingStatus(id: Long): Boolean
    fun stopLimitOrderTrading(id: Long)
    suspend fun runLimitOrderTrading(tradingModelsDto: TradingModelsDto)
    fun closeApi()

}