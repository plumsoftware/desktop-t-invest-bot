package ru.plumsoftware.ui.presentation.screens.market

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import ru.plumsoftware.core.brokerage.market.MarketRepository
import ru.plumsoftware.core.brokerage.model.Trading
import ru.plumsoftware.core.brokerage.model.TradingModel
import ru.plumsoftware.core.settings.repository.SettingsRepository
import ru.plumsoftware.log.model.LogMode
import ru.plumsoftware.log.model.LogTradingOperation
import ru.plumsoftware.log.repository.LogRepository
import ru.plumsoftware.ui.presentation.screens.market.model.Effect
import ru.plumsoftware.ui.presentation.screens.market.model.Event
import ru.plumsoftware.ui.presentation.screens.market.model.Model
import ru.tinkoff.piapi.contract.v1.Instrument
import ru.tinkoff.piapi.contract.v1.MoneyValue
import ru.tinkoff.piapi.contract.v1.OrderState
import ru.tinkoff.piapi.core.InvestApi
import ru.tinkoff.piapi.core.models.Money
import ru.tinkoff.piapi.core.models.Portfolio
import java.math.BigDecimal


class MarketViewModel(
    private val settingsRepository: SettingsRepository,
    private val marketRepository: MarketRepository,
    private val logRepository: LogRepository
) : ViewModel() {
    val model = MutableStateFlow(Model())
    val effect = MutableSharedFlow<Effect>()

    private val supervisorIOTradingContext =
        Dispatchers.IO + SupervisorJob() + CoroutineName("trading coroutine") //Получение цены/покупка/продажа
    private val supervisorDefaultTradingContext =
        Dispatchers.Default + SupervisorJob() + CoroutineName("trading price coroutine") //Расчёт цены

    fun onEvent(event: Event) {
        when (event) {
            is Event.AddToTrading -> {
                val isTrading = event.isTrading

                if (isTrading) {
                    val increase = event.increase.toFloatOrNull()
                    val decrease = event.decrease.toFloatOrNull()
                    if (increase != null && decrease != null) {
                        model.value.tradingModels.add(
                            TradingModel(
                                figi = event.figi,
                                countLots = event.countLots,
                                increase = increase,
                                decrease = decrease
                            )
                        )
                    }
                } else {
                    model.value.tradingModels.removeIf { it.figi == event.figi }
                }
            }

            Event.Back -> {
                viewModelScope.launch {
                    effect.emit(Effect.Back)
                }
            }

            Event.Init -> {
                viewModelScope.launch {
                    val settings = settingsRepository.getSettings()
                    val accountId: String = marketRepository.getMarketAccount()
                    val api: InvestApi? = if (model.value.api == null)
                        marketRepository.getMarketApi(settings.apiTokens.token)
                    else
                        model.value.api

                    model.update {
                        it.copy(
                            api = api,
                            accountId = accountId
                        )
                    }

                    portfolio()
                }
            }

            is Event.StartTrading -> {
                model.update {
                    it.copy(
                        isStartTrading = event.isStartTrading
                    )
                }

                var msg = ""
                msg += if (model.value.isStartTrading)
                    "Торги начаты."
                else
                    "Торги остановлены."
                viewModelScope.launch {
                    effect.emit(Effect.ShowSnackbar(msg))
                }

                runTradingLimitOrder()
            }
        }
    }

    private fun portfolio() {
        val api = model.value.api
        if (api != null) {
            viewModelScope.launch(Dispatchers.IO) {
                while (!model.value.isStartTrading) {
                    delay(Trading.DEFAULT_UPDATE_PORTFOLIO_TICK)
                    val portfolio =
                        marketRepository.getPortfolio(api = api, accountId = model.value.accountId)
                    withContext(Dispatchers.Main) {
                        model.update {
                            it.copy(
                                portfolio = portfolio,
                            )
                        }
                    }
                }
            }
        }
    }

    fun getInstrumentByFigi(figi: String): Instrument? {
        val api = model.value.api
        return if (api != null) {
            marketRepository.getInstrumentByFigi(api = api, figi = figi)
        } else null
    }

    private fun runTrading() {

        val tradingModels = model.value.tradingModels.toList()
        val api = model.value.api
        val accountId = model.value.accountId

        if (api != null) {
            val startPricesMap = mutableMapOf<TradingModel, Money>()
            val isSoldMap = mutableMapOf<TradingModel, Boolean>()
            val lastOrdersIdMap = mutableMapOf<TradingModel, String>()
            val jobs = mutableListOf<Job>()

            var portfolio: Portfolio = marketRepository.getPortfolio(
                api = api,
                accountId = accountId
            )
            var positions = portfolio.positions

            tradingModels.forEachIndexed { _, tradingModel ->
                positions.forEachIndexed { _, position ->
                    if (tradingModel.figi == position.figi) {
                        startPricesMap[tradingModel] = position.currentPrice
                        isSoldMap[tradingModel] = false
                    }
                }
            }

            val job = viewModelScope.launch(supervisorIOTradingContext) {
                while (model.value.isStartTrading) {
                    delay(Trading.DEFAULT_TRADING_TICK_MS)
                    portfolio = marketRepository.getPortfolio(
                        api = api,
                        accountId = accountId
                    )
                    positions = portfolio.positions

                    tradingModels.forEachIndexed { _, tradingModel ->
                        positions.forEachIndexed { _, position ->
                            if (tradingModel.figi == position.figi) {
                                val job = viewModelScope.launch(supervisorIOTradingContext) {
                                    val instrument = getInstrumentByFigi(figi = tradingModel.figi)
                                    val isSold = isSoldMap.getOrDefault(tradingModel, false)
                                    val lastOrderId = lastOrdersIdMap.getOrDefault(tradingModel, "")
                                    val lots = tradingModel.countLots
                                    withContext(supervisorDefaultTradingContext) {
                                        val increasePercent = tradingModel.increase
                                        val decreasePercent = tradingModel.decrease
                                        var operation = ""
                                        val currentPrice = position.currentPrice.value.toDouble()
                                        val oldPrice =
                                            startPricesMap[tradingModel]?.value?.toDouble() ?: 0.0

                                        val percentChange =
                                            ((currentPrice - oldPrice) / oldPrice) * 100

                                        println("name: ${instrument?.name}" + "\n" + "figi: ${tradingModel.figi}" + "\n" + "last price: $oldPrice" + "\n" + "current price: $currentPrice" + "\n" + "percent change: $percentChange" + "\n" + "=====================================")

                                        val bidDecimal = BigDecimal.valueOf(currentPrice)
                                        val moneyValue = MoneyValue.newBuilder()
                                            .setCurrency("RUB")
                                            .setUnits(bidDecimal.toLong())
                                            .setNano(
                                                bidDecimal.remainder(BigDecimal.ONE)
                                                    .multiply(BigDecimal.valueOf(1_000_000_000))
                                                    .toInt()
                                            )
                                            .build()

                                        if (percentChange < 0) {
                                            if ((percentChange * -1) >= decreasePercent) {
                                                //BUY
                                                val ordersSync =
                                                    api.ordersService.getOrdersSync(accountId)
                                                if (ordersSync.isEmpty()) {
                                                    isSoldMap[tradingModel] = false
                                                } else {
                                                    ordersSync.forEach { order ->
                                                        if (order.orderId != lastOrderId && order.figi == tradingModel.figi) {
                                                            isSoldMap[tradingModel] = false
                                                        }
                                                    }
                                                }
                                                if (isSold) {
                                                    val money = Money.fromResponse(moneyValue)
                                                    withContext(supervisorIOTradingContext) {
                                                        val orderId = marketRepository.buyWithLots(
                                                            api = api,
                                                            lots = lots,
                                                            accountId = model.value.accountId,
                                                            figi = tradingModel.figi,
                                                        )
                                                        lastOrdersIdMap[tradingModel] =
                                                            orderId.first

                                                        //Change old price
                                                        startPricesMap[tradingModel] = money
                                                    }

                                                    operation = "BUY"
                                                    println("-->$operation<--\n=====================================")

                                                }
                                            } else {
                                                //HOLD
                                                operation = "HOLD"
                                                println("-->$operation<--\n=====================================")
                                            }
                                        } else if (percentChange > 0) {
                                            if (percentChange >= increasePercent) {
                                                //SELL
                                                val ordersSync: MutableList<OrderState> =
                                                    api.ordersService.getOrdersSync(accountId)
                                                if (ordersSync.isEmpty()) {
                                                    isSoldMap[tradingModel] = true
                                                } else {
                                                    ordersSync.forEach { order ->
                                                        if (order.orderId != lastOrderId && order.figi == tradingModel.figi) {
                                                            isSoldMap[tradingModel] = true
                                                        }
                                                    }
                                                }
                                                if (!isSold) {
                                                    val money = Money.fromResponse(moneyValue)
                                                    withContext(supervisorIOTradingContext) {
                                                        val orderId = marketRepository.sellWithLots(
                                                            api = api,
                                                            lots = lots,
                                                            accountId = model.value.accountId,
                                                            figi = tradingModel.figi,
                                                        )
                                                        lastOrdersIdMap[tradingModel] =
                                                            orderId.first

                                                        //Change old price
                                                        startPricesMap[tradingModel] = money
                                                    }
                                                    operation = "SELL"

                                                    println("-->$operation<--\n=====================================")
                                                }
                                            } else {
                                                //HOLD
                                                operation = "HOLD"
                                                println("-->$operation<--\n=====================================")
                                            }
                                        } else {
                                            //HOLD
                                            operation = "HOLD"
                                            println("-->$operation<--\n=====================================")
                                        }

                                        withContext(supervisorIOTradingContext) {
                                            logRepository.write(
                                                logMode = LogMode.MARKET,
                                                logTradingOperation = LogTradingOperation(
                                                    accountId = model.value.accountId,
                                                    name = instrument?.name
                                                        ?: "${tradingModel.figi}_name_unspecified",
                                                    figi = tradingModel.figi,
                                                    countLots = tradingModel.countLots.toString(),
                                                    currentPrice = currentPrice.toString(),
                                                    lastPrice = startPricesMap[tradingModel]?.value.toString() + " " + startPricesMap[tradingModel]?.currency,
                                                    percentIncrease = tradingModel.increase.toString(),
                                                    percentDecrease = tradingModel.decrease.toString(),
                                                    currentPercentChange = percentChange.toString(),
                                                    operation = operation
                                                )
                                            )
                                        }
                                    }
                                }
                                jobs.add(job)
                            }
                        }
                    }
                }
            }

            if (!model.value.isStartTrading) {
                viewModelScope.launch(supervisorIOTradingContext) {
                    val totalAmountPortfolio = marketRepository.getPortfolio(
                        api = api,
                        accountId = accountId
                    ).totalAmountPortfolio
                    withContext(viewModelScope.coroutineContext) {
                        model.update {
                            it.copy(stopTradingTotalAmountPortfolio = totalAmountPortfolio)
                        }
                    }
                }
                job.cancelChildren()
                job.cancel()
                portfolio()
            }
        }
    }

    private fun runTradingLimitOrder() {
        val tradingModels = model.value.tradingModels.toList()
        val api = model.value.api
        val accountId = model.value.accountId

        if (api != null) {

            val jobs = mutableListOf<Job>()

            tradingModels.forEach { tradingModel ->

                val quo =
                    api.marketDataService.getLastPricesSync(listOf(tradingModel.figi))[0].price
                var assetOldPrice = Money.fromResponse(
                    MoneyValue
                        .newBuilder()
                        .setCurrency("RUB")
                        .setUnits(quo.units)
                        .setNano(quo.nano)
                        .build()
                )

                val lots = tradingModel.countLots
                val instrument = getInstrumentByFigi(figi = tradingModel.figi)
                val increasePercent = tradingModel.increase
                val decreasePercent = tradingModel.decrease

                var sellOrderId = ""
                var buyOrderId = ""

                val job = viewModelScope.launch(supervisorIOTradingContext) {
                    while (true) {
                        if (!model.value.isStartTrading) break
                        delay(Trading.DEFAULT_TRADING_TICK_MS)
                        val quotation =
                            api.marketDataService.getLastPricesSync(listOf(tradingModel.figi))[0].price
                        val assetCurrentPrice = Money.fromResponse(
                            MoneyValue.newBuilder().setCurrency("RUB").setUnits(quotation.units)
                                .setNano(quotation.nano).build()
                        )
                        var operation = ""

                        println("lots: $lots")
                        println("instrument: ${instrument?.name}")
                        println("increasePercent: $increasePercent")
                        println("decreasePercent: $decreasePercent")

                        println("assetOldPrice: ${assetOldPrice?.value}")
                        println("assetCurrentPrice: ${assetCurrentPrice.value}")

                        if (assetOldPrice != null) {
                            val percentChange =
                                ((assetCurrentPrice.value.toDouble() - assetOldPrice.value.toDouble()) / assetOldPrice.value.toDouble()) * 100

                            println("percentChange: $percentChange")

                            if (percentChange > 0) {
                                //SELL
                                if (percentChange >= increasePercent) {
                                    val orders = api.ordersService.getOrdersSync(accountId)
                                    val orderState: OrderState? =
                                        orders.find { it.figi == tradingModel.figi && it.orderId == buyOrderId }

                                    buyOrderId = if (orderState != null) {
                                        orderState.orderId
                                    } else {
                                        ""
                                    }

                                    if (buyOrderId.isEmpty())
                                        withContext(supervisorIOTradingContext) {
                                            val pair = marketRepository.sellWithLots(
                                                api = api,
                                                lots = lots,
                                                accountId = accountId,
                                                figi = tradingModel.figi,
                                            )

                                            //Change old price
                                            assetOldPrice = assetCurrentPrice
                                            sellOrderId = pair.first

                                            operation = "SELL"
                                            println("SELL orderId ${pair.first}; money: ${pair.second.value}")
                                        }
                                    else {
                                        operation = "HOLD"
                                        println("HOLD")
                                    }
                                } else {
                                    operation = "HOLD"
                                    println("HOLD")
                                }
                            }
                            else if (percentChange < 0) {
                                //BUY
                                if ((percentChange * -1) >= decreasePercent) {
                                    val orders = api.ordersService.getOrdersSync(accountId)
                                    val orderState: OrderState? =
                                        orders.find { it.figi == tradingModel.figi && it.orderId == sellOrderId }

                                    sellOrderId = if (orderState != null) {
                                        orderState.orderId
                                    } else {
                                        ""
                                    }
                                    if (sellOrderId.isEmpty())
                                        withContext(supervisorIOTradingContext) {
                                            val pair = marketRepository.buyWithLots(
                                                api = api,
                                                lots = lots,
                                                accountId = accountId,
                                                figi = tradingModel.figi,
                                            )

                                            //Change old price
                                            assetOldPrice = assetCurrentPrice
                                            buyOrderId = pair.first

                                            operation = "BUY"
                                            println("BUY orderId ${pair.first}; money: ${pair.second.value}")
                                        }
                                    else {
                                        operation = "HOLD"
                                        println("HOLD")
                                    }
                                } else {
                                    operation = "HOLD"
                                    println("HOLD")
                                }
                            }
                            else {
                                println("HOLD")
                            }
                            println("===========================================")

                            withContext(supervisorIOTradingContext) {
                                logRepository.write(
                                    logMode = LogMode.MARKET,
                                    logTradingOperation = LogTradingOperation(
                                        accountId = model.value.accountId,
                                        name = instrument?.name
                                            ?: "${tradingModel.figi}_name_unspecified",
                                        figi = tradingModel.figi,
                                        countLots = tradingModel.countLots.toString(),
                                        currentPrice = assetCurrentPrice?.value.toString() + " " + assetCurrentPrice?.currency,
                                        lastPrice = assetOldPrice?.value.toString() + " " + assetOldPrice?.currency,
                                        percentIncrease = tradingModel.increase.toString(),
                                        percentDecrease = tradingModel.decrease.toString(),
                                        currentPercentChange = percentChange.toString(),
                                        operation = operation
                                    )
                                )
                            }
                        }
                    }
                }
                jobs.add(job)
            }

            if (!model.value.isStartTrading) {
                jobs.forEach { it.cancel() }
                viewModelScope.launch(supervisorIOTradingContext) {
                    val totalAmountPortfolio = marketRepository.getPortfolio(
                        api = api,
                        accountId = accountId
                    ).totalAmountPortfolio
                    withContext(viewModelScope.coroutineContext) {
                        model.update {
                            it.copy(stopTradingTotalAmountPortfolio = totalAmountPortfolio)
                        }
                    }
                }
            }
        }
    }
}