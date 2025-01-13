package ru.plumsoftware.core.brokerage.market

import ru.tinkoff.piapi.contract.v1.Account
import ru.tinkoff.piapi.contract.v1.Instrument
import ru.tinkoff.piapi.core.InvestApi
import ru.tinkoff.piapi.core.models.Money
import ru.tinkoff.piapi.core.models.Portfolio

interface MarketRepository {
    fun getMarketApi(token: String): InvestApi
    suspend fun saveMarketAccountId(accountId: String)
    suspend fun getMarketAccounts(api: InvestApi) : List<Account>
    suspend fun getMarketAccount() : String
    fun getPortfolio(api: InvestApi, accountId: String) : Portfolio
    fun getInstrumentByFigi(api: InvestApi, figi: String) : Instrument

    //Sell&Buy
    suspend fun buyWithLots(
        api: InvestApi,
        lots: Int,
        accountId: String,
        figi: String,
        price: Money
    ) : String
    suspend fun sellWithLots(
        api: InvestApi,
        lots: Int,
        accountId: String,
        figi: String,
        price: Money
    ) : String
}