package ru.plumsoftware.core.brokerage.market

import kotlinx.serialization.json.Json
import ru.plumsoftware.core.brokerage.market.model.MarketConfig
import ru.plumsoftware.core.brokerage.market.model.Acc
import ru.plumsoftware.core.brokerage.market.model.Path
import ru.tinkoff.piapi.contract.v1.Account
import ru.tinkoff.piapi.contract.v1.Instrument
import ru.tinkoff.piapi.contract.v1.OrderDirection
import ru.tinkoff.piapi.contract.v1.OrderType
import ru.tinkoff.piapi.contract.v1.Quotation
import ru.tinkoff.piapi.core.InvestApi
import ru.tinkoff.piapi.core.models.Money
import ru.tinkoff.piapi.core.models.Portfolio
import java.io.File
import java.math.BigDecimal
import java.util.UUID

class MarketRepositoryImpl : MarketRepository {

    init {
        createEndFileIfNot()
    }

    override fun getMarketApi(token: String): InvestApi {
        return InvestApi.create(token)
    }

    override suspend fun saveMarketAccountId(accountId: String) {
        val file = File(Path.mainPathToMarketConfigFile)

        val get = Json.decodeFromString<MarketConfig>(file.readText())

        file.writeBytes(encode(get.copy(acc = Acc(accountId))).encodeToByteArray())
    }

    override suspend fun getMarketAccounts(api: InvestApi) : List<Account> {
        return api.userService.accountsSync
    }

    override suspend fun getMarketAccount(): String {
        val file = File(Path.mainPathToMarketConfigFile)
        val get = Json.decodeFromString<MarketConfig>(file.readText())
        return get.acc.accountId
    }

    override fun getPortfolio(api: InvestApi, accountId: String): Portfolio {
        return api.operationsService.getPortfolioSync(accountId)
    }

    override fun getInstrumentByFigi(api: InvestApi, figi: String): Instrument {
        return api.instrumentsService.getInstrumentByFigiSync(figi)
    }

    override suspend fun buyWithLots(
        api: InvestApi,
        lots: Int,
        accountId: String,
        figi: String,
        price: Money
    ) {
        val bigDecimal = price.value
        val quotation = Quotation.newBuilder()
            .setUnits(bigDecimal.toLong())
            .setNano(
                bigDecimal.remainder(BigDecimal.ONE).multiply(BigDecimal.valueOf(1_000_000_000))
                    .toInt()
            )
            .build()

        val orderId = api.ordersService
            .postOrderSync(
                figi,
                lots.toLong(),
                quotation,
                OrderDirection.ORDER_DIRECTION_BUY,
                accountId,
                OrderType.ORDER_TYPE_MARKET,
                UUID.randomUUID().toString()
            ).getOrderId()
        println("Заявка на покупку $lots лотов инструмента с figi $figi номер: $orderId")
    }

    override suspend fun sellWithLots(
        api: InvestApi,
        lots: Int,
        accountId: String,
        figi: String,
        price: Money
    ) {
        val bigDecimal = price.value
        val quotation = Quotation.newBuilder()
            .setUnits(bigDecimal.toLong())
            .setNano(
                bigDecimal.remainder(BigDecimal.ONE).multiply(BigDecimal.valueOf(1_000_000_000))
                    .toInt()
            )
            .build()

        val orderId = api.ordersService
            .postOrderSync(
                figi,
                lots.toLong(),
                quotation,
                OrderDirection.ORDER_DIRECTION_SELL,
                accountId,
                OrderType.ORDER_TYPE_MARKET,
                UUID.randomUUID().toString()
            ).getOrderId()

        println("Заявка на продажу $lots лотов инструмента с figi $figi номер: $orderId")
    }

    private fun encode(marketConfig: MarketConfig): String {
        val stringBuffer = StringBuffer()
        stringBuffer
            .append("{\n")
            .append("\t\"account\": {\n")
            .append("\t\t\"account_id\": \"${marketConfig.acc.accountId}\"\n")
            .append("\t}\n")
            .append("}")

        return stringBuffer.toString()
    }

    private fun createEndFileIfNot() {
        val folder = File(Path.mainPathToMarketConfigFolder)

        if (!folder.exists()) {
            val mkdir = folder.mkdir()

            if (mkdir) {
                val file = File(Path.mainPathToMarketConfigFile)
                if (!file.exists()) {
                    file.createNewFile()
                    file.writeBytes(encode(MarketConfig(Acc())).encodeToByteArray())
                }
            }
        } else {
            val file = File(Path.mainPathToMarketConfigFile)
            if (!file.exists()) {
                file.createNewFile()
                file.writeBytes(encode(MarketConfig(Acc())).encodeToByteArray())
            }
        }
    }
}