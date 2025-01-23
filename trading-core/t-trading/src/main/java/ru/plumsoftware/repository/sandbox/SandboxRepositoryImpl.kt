package ru.plumsoftware.repository.sandbox

import ru.tinkoff.piapi.contract.v1.Account
import ru.tinkoff.piapi.contract.v1.InstrumentShort
import ru.tinkoff.piapi.contract.v1.MoneyValue
import ru.tinkoff.piapi.core.InvestApi
import ru.tinkoff.piapi.core.models.Portfolio
import ru.tinkoff.piapi.core.models.Positions

class SandboxRepositoryImpl(token: String) : SandboxRepository {

    private val investApi: InvestApi = InvestApi.createSandbox(token)

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
}