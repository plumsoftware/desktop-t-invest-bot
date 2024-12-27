package ru.plumsoftware.core.brokerage.sandbox.repository

import ru.tinkoff.piapi.contract.v1.Account
import ru.tinkoff.piapi.core.InvestApi
import ru.tinkoff.piapi.core.models.Portfolio
import ru.tinkoff.piapi.core.models.Positions

interface SandboxRepository {
    fun getSandboxApi(token: String): InvestApi

    suspend fun saveSandboxAccountId(
        accountId: String,
    )
    suspend fun getLastSandboxAccountId(): String

    fun sandboxService(sandboxApi: InvestApi, figi: String): String
    fun closeAccount(sandboxApi: InvestApi, accountId: String)
    fun closeAll(sandboxApi: InvestApi)
    fun getSandboxAccounts(sandboxApi: InvestApi): List<Account>

    fun getPortfolio(sandboxApi: InvestApi, index: Int = 0): Portfolio
    fun getPositions(sandboxApi: InvestApi, index: Int = 0): Positions

    fun addMoney(value: Int, sandboxApi: InvestApi, accountId: String)
}