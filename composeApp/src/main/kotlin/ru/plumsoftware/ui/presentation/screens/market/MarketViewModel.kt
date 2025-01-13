package ru.plumsoftware.ui.presentation.screens.market

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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

                runTrading()
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
                            it.copy(portfolio = portfolio)
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
                                val instrument = getInstrumentByFigi(figi = tradingModel.figi)
                                val isSold = isSoldMap.getOrDefault(tradingModel, false)
                                val lots = tradingModel.countLots
                                withContext(supervisorDefaultTradingContext) {
                                    val increasePercent = tradingModel.increase
                                    val decreasePercent = tradingModel.decrease

                                    val currentPrice = position.currentPrice.value.toDouble()
                                    val oldPrice =
                                        startPricesMap[tradingModel]?.value?.toDouble() ?: 0.0

                                    val percentChange =
                                        ((currentPrice - oldPrice) / oldPrice) * 100

                                    println("name: ${instrument?.name}" + "\n" + "figi: ${tradingModel.figi}" + "\n" + "last price: $oldPrice" + "\n" + "current price: $currentPrice" + "\n" + "percent change: $percentChange" + "\n" + "=====================================")

                                    var operation = ""

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
                                            if (isSold) {
                                                val money = Money.fromResponse(moneyValue)
                                                withContext(supervisorIOTradingContext) {
                                                    marketRepository.buyWithLots(
                                                        api = api,
                                                        lots = lots,
                                                        accountId = model.value.accountId,
                                                        figi = tradingModel.figi,
                                                        price = money
                                                    )
                                                }

                                                operation = "BUY"
                                                println("-->$operation<--\n=====================================")

                                                isSoldMap[tradingModel] = false

                                                //Change old price
                                                startPricesMap[tradingModel] = money

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
                                    if (percentChange > 0) {
                                        if (percentChange >= increasePercent) {
                                            //SELL
                                            if (!isSold) {
                                                val money = Money.fromResponse(moneyValue)
                                                withContext(supervisorIOTradingContext) {
                                                    marketRepository.sellWithLots(
                                                        api = api,
                                                        lots = tradingModel.countLots,
                                                        accountId = model.value.accountId,
                                                        figi = tradingModel.figi,
                                                        price = money
                                                    )
                                                }
                                                operation = "SELL"
                                                isSoldMap[tradingModel] = true
                                                println("-->$operation<--\n=====================================")

                                                //Change old price
                                                startPricesMap[tradingModel] = money
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
                                    if (percentChange == 0.0) {
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
                        }
                    }
                }
            }

            if (!model.value.isStartTrading) {
                job.cancel()
            }
        }
    }
}