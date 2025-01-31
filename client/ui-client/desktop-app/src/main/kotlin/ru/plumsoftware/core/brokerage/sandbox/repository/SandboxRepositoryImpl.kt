package ru.plumsoftware.core.brokerage.sandbox.repository

import kotlinx.serialization.json.Json
import ru.plumsoftware.core.brokerage.sandbox.model.Acc
import ru.plumsoftware.core.brokerage.sandbox.model.Path
import ru.plumsoftware.core.brokerage.sandbox.model.SandboxConfig
import ru.tinkoff.piapi.contract.v1.*
import ru.tinkoff.piapi.core.InvestApi
import ru.tinkoff.piapi.core.models.Money
import ru.tinkoff.piapi.core.models.Portfolio
import ru.tinkoff.piapi.core.models.Positions
import ru.tinkoff.piapi.core.models.Quantity
import java.io.File
import java.math.BigDecimal
import java.util.UUID


class SandboxRepositoryImpl : SandboxRepository {

    init {
        createEndFileIfNot()
    }

    override fun getSandboxApi(token: String): InvestApi {
        return InvestApi.createSandbox(token)
    }

    override suspend fun saveSandboxAccountId(accountId: String) {
        val file = File(Path.mainPathToSandboxConfigFile)

        val get = Json.decodeFromString<SandboxConfig>(file.readText())

        file.writeBytes(encode(get.copy(acc = Acc(accountId))).encodeToByteArray())
    }

    override suspend fun getLastSandboxAccountId(): String {
        val file = File(Path.mainPathToSandboxConfigFile)
        val text = file.readText()
        return Json.decodeFromString<SandboxConfig>(text).acc.accountId
    }

    override fun sandboxService(sandboxApi: InvestApi, figi: String): String {
        val accountId = sandboxApi.sandboxService.openAccountSync()

        val accounts = sandboxApi.userService.accountsSync
        for (account in accounts) {
            println("sandbox account id: ${account.id}, access level: ${account.accessLevel.name}")
        }

        println("тариф должен быть sandbox. фактический тариф: ${sandboxApi.userService.infoSync.tariff}")

        return accountId
    }

    override fun closeAccount(sandboxApi: InvestApi, accountId: String) {
        sandboxApi.sandboxService.closeAccountSync(accountId)
    }

    override fun closeAll(sandboxApi: InvestApi) {
        sandboxApi.sandboxService.accountsSync.forEach { account ->
            sandboxApi.sandboxService.closeAccountSync(account.id)
        }
    }

    override fun getSandboxAccounts(sandboxApi: InvestApi): List<Account> {
        return sandboxApi.sandboxService.accountsSync
    }

    override fun getPortfolio(sandboxApi: InvestApi, accountId: String): Portfolio {
        val portfolio = sandboxApi.operationsService.getPortfolioSync(accountId)
        return portfolio
    }

    override fun getPositions(sandboxApi: InvestApi, index: Int): Positions {
        val accounts = sandboxApi.userService.accountsSync
        val mainAccount = accounts[index].id

        //Получаем список позиций
        val positions = sandboxApi.operationsService.getPositionsSync(mainAccount)
        return positions
    }

    override fun addMoney(value: Int, sandboxApi: InvestApi, accountId: String) {
        sandboxApi.sandboxService.payIn(
            accountId,
            MoneyValue.newBuilder().setUnits(value.toLong()).setCurrency("RUB").build()
        )
    }

    override suspend fun createOrder(sandboxApi: InvestApi, accountId: String, id: String) {

        val lastPrice = sandboxApi.marketDataService.getLastPricesSync(listOf("figi"))[0].price
        val minPriceIncrement =
            sandboxApi.instrumentsService.getInstrumentByFigiSync("figi").minPriceIncrement

        val price = Quantity.ofQuotation(lastPrice)
            .subtract(
                Quantity.ofQuotation(minPriceIncrement)
                    .mapValue { minPriceBigDecimal: BigDecimal ->
                        minPriceBigDecimal.multiply(
                            BigDecimal.TEN.multiply(BigDecimal.TEN)
                        )
                    }
            )
            .toQuotation()

        val orderId = sandboxApi.ordersService
            .postOrderSync(
                "figi",
                1,
                price,
                OrderDirection.ORDER_DIRECTION_BUY,
                accountId,
                OrderType.ORDER_TYPE_LIMIT,
                UUID.randomUUID().toString()
            ).getOrderId()
    }

    override suspend fun getInstrumentsBy(
        sandboxApi: InvestApi,
        id: String
    ): List<InstrumentShort> {
        val instruments = sandboxApi.instrumentsService.findInstrumentSync(id)
        return instruments.toList()
    }

    override suspend fun buyWithMoney(
        sandboxApi: InvestApi,
        money: String,
        accountId: String,
        figi: String
    ) {
        val lastPrice = sandboxApi.marketDataService.getLastPricesSync(listOf(figi))[0].price

        val minPriceIncrement =
            sandboxApi.instrumentsService.getInstrumentByFigiSync(figi).minPriceIncrement

        val price = Quantity
            .ofQuotation(lastPrice)
            .subtract(
                Quantity
                    .ofQuotation(minPriceIncrement)
                    .mapValue { minPriceBigDecimal ->
                        minPriceBigDecimal.multiply(BigDecimal.TEN.multiply(BigDecimal.TEN))
                    }
            )
            .toQuotation()

        var orderId = sandboxApi.ordersService
            .postOrderSync(
                figi,
                1,
                price,
                OrderDirection.ORDER_DIRECTION_BUY,
                accountId,
                OrderType.ORDER_TYPE_MARKET,
                UUID.randomUUID().toString()
            ).getOrderId()
    }

    override suspend fun sellWithMoney(
        sandboxApi: InvestApi,
        money: String,
        accountId: String,
        figi: String
    ) {
    }

    override suspend fun buyWithLots(
        sandboxApi: InvestApi,
        lots: Int,
        accountId: String,

        figi: String,
        price: Money
    ) {
        val lastPrice = sandboxApi.marketDataService.getLastPricesSync(listOf(figi))[0].price
        val instrument = sandboxApi.instrumentsService.getInstrumentByFigiSync(figi)
        val minPriceIncrement =
            instrument.minPriceIncrement

        val bigDecimal = price.value
        val quotation = Quotation.newBuilder()
            .setUnits(bigDecimal.toLong())
            .setNano(
                bigDecimal.remainder(BigDecimal.ONE).multiply(BigDecimal.valueOf(1_000_000_000))
                    .toInt()
            )
            .build()

        println("+++++++++++++++++++++++>Buy ${instrument.name} with price ${quotation}<+++++++++++++++++++++++")

        val orderId = sandboxApi.ordersService
            .postOrderSync(
                figi,
                lots.toLong(),
                quotation,
                OrderDirection.ORDER_DIRECTION_BUY,
                accountId,
                OrderType.ORDER_TYPE_MARKET,
                UUID.randomUUID().toString()
            ).getOrderId()
        println(orderId)
    }

    override suspend fun sellWithLots(
        sandboxApi: InvestApi,
        lots: Int,
        accountId: String,
        figi: String,
        price: Money
    ) {
        val lastPrice = sandboxApi.marketDataService.getLastPricesSync(listOf(figi))[0].price
        val instrument = sandboxApi.instrumentsService.getInstrumentByFigiSync(figi)
        val minPriceIncrement =
            instrument.minPriceIncrement

        val bigDecimal = price.value
        val quotation = Quotation.newBuilder()
            .setUnits(bigDecimal.toLong())
            .setNano(
                bigDecimal.remainder(BigDecimal.ONE).multiply(BigDecimal.valueOf(1_000_000_000))
                    .toInt()
            )
            .build()

        println("+++++++++++++++++++++++>Sell ${instrument.name} with price ${quotation}<+++++++++++++++++++++++")

        val orderId = sandboxApi.ordersService
            .postOrderSync(
                figi,
                lots.toLong(),
                quotation,
                OrderDirection.ORDER_DIRECTION_SELL,
                accountId,
                OrderType.ORDER_TYPE_MARKET,
                UUID.randomUUID().toString()
            ).getOrderId()
        println(orderId)
    }

    override suspend fun buy(sandboxApi: InvestApi, lots: Int, accountId: String, figi: String) {
        val lastPrice = sandboxApi.marketDataService.getLastPricesSync(listOf(figi))[0].price
        val instrument = sandboxApi.instrumentsService.getInstrumentByFigiSync(figi)
        val minPriceIncrement =
            instrument.minPriceIncrement

        val price = Quantity
            .ofQuotation(lastPrice)
            .subtract(
                Quantity
                    .ofQuotation(minPriceIncrement)
                    .mapValue { minPriceBigDecimal ->
                        minPriceBigDecimal.multiply(BigDecimal.TEN.multiply(BigDecimal.TEN))
                    }
            )
            .toQuotation()

        val orderId = sandboxApi.ordersService
            .postOrderSync(
                figi,
                lots.toLong(),
                price,
                OrderDirection.ORDER_DIRECTION_BUY,
                accountId,
                OrderType.ORDER_TYPE_MARKET,
                UUID.randomUUID().toString()
            ).getOrderId()
        println("Order id: $orderId")
    }

    private fun encode(sandboxConfig: SandboxConfig): String {
        val stringBuffer = StringBuffer()
        stringBuffer
            .append("{\n")
            .append("\t\"account\": {\n")
            .append("\t\t\"account_id\": \"${sandboxConfig.acc.accountId}\"\n")
            .append("\t}\n")
            .append("}")

        return stringBuffer.toString()
    }

    private fun createEndFileIfNot() {
        val folder = File(Path.mainPathToSandboxConfigFolder)

        if (!folder.exists()) {
            val mkdir = folder.mkdir()

            if (mkdir) {
                val file = File(Path.mainPathToSandboxConfigFile)
                if (!file.exists()) {
                    file.createNewFile()
                    file.writeBytes(encode(SandboxConfig(Acc())).encodeToByteArray())
                }
            }
        } else {
            val file = File(Path.mainPathToSandboxConfigFile)
            if (!file.exists()) {
                file.createNewFile()
                file.writeBytes(encode(SandboxConfig(Acc())).encodeToByteArray())
            }
        }
    }
}