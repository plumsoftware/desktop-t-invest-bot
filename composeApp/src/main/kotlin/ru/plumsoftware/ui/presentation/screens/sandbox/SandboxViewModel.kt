package ru.plumsoftware.ui.presentation.screens.sandbox

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import ru.plumsoftware.core.brokerage.model.Trading
import ru.plumsoftware.core.brokerage.model.TradingModel
import ru.plumsoftware.core.brokerage.sandbox.repository.SandboxRepository
import ru.plumsoftware.core.settings.repository.SettingsRepository
import ru.plumsoftware.log.model.LogSandbox
import ru.plumsoftware.log.repository.LogRepository
import ru.plumsoftware.ui.presentation.screens.sandbox.model.Effect
import ru.plumsoftware.ui.presentation.screens.sandbox.model.Event
import ru.plumsoftware.ui.presentation.screens.sandbox.model.Model
import ru.tinkoff.piapi.contract.v1.Instrument
import ru.tinkoff.piapi.contract.v1.InstrumentShort
import ru.tinkoff.piapi.contract.v1.MoneyValue
import ru.tinkoff.piapi.core.InvestApi
import ru.tinkoff.piapi.core.models.Money
import ru.tinkoff.piapi.core.models.Portfolio
import ru.tinkoff.piapi.core.models.Position
import java.math.BigDecimal
import kotlin.time.Duration.Companion.seconds

class SandboxViewModel(
    private val sandboxRepository: SandboxRepository,
    private val settingsRepository: SettingsRepository,
    private val logRepository: LogRepository
) : ViewModel() {

    val effect = MutableSharedFlow<Effect>()
    val model = MutableStateFlow(Model())

    private val supervisorIOTradingContext =
        Dispatchers.IO + SupervisorJob() + CoroutineName("trading coroutine") //Получение цены/покупка/продажа
    private val supervisorDefaultTradingContext =
        Dispatchers.Default + SupervisorJob() + CoroutineName("trading price coroutine") //Расчёт цены


    fun onEvent(event: Event) {
        when (event) {
            Event.Back -> {
                viewModelScope.launch {
                    effect.emit(Effect.Back)
                }
            }

            Event.Init -> {
                viewModelScope.launch {
                    val settings = settingsRepository.getSettings()
                    val lastSandboxAccountId: String = sandboxRepository.getLastSandboxAccountId()

                    val sandboxApi: InvestApi? = if (model.value.sandboxApi == null)
                        sandboxRepository.getSandboxApi(settings.apiTokens.sandboxToken)
                    else
                        model.value.sandboxApi

                    if (lastSandboxAccountId.isEmpty()) {
                        if (sandboxApi != null) {
                            val accountId: String =
                                sandboxRepository.sandboxService(sandboxApi = sandboxApi, figi = "")
                            sandboxRepository.saveSandboxAccountId(accountId = accountId)
                            val portfolio = sandboxRepository.getPortfolio(sandboxApi, accountId)
                            val positions: MutableList<Position> =
                                portfolio.positions
                                    .reversed()
                                    .toMutableList()

                            model.update {
                                it.copy(
                                    accountId = accountId,
                                    sandboxApi = sandboxApi,
                                    portfolio = portfolio,
                                    positions = positions
                                )
                            }
                        }
                    } else {
                        if (sandboxApi != null) {
                            val portfolio =
                                sandboxRepository.getPortfolio(sandboxApi, lastSandboxAccountId)
                            val positions: MutableList<Position> = portfolio.positions
                            model.update {
                                it.copy(
                                    accountId = lastSandboxAccountId,
                                    sandboxApi = sandboxApi,
                                    portfolio = portfolio,
                                    positions = positions
                                )
                            }
                        }
                    }
                }
            }

            Event.CloseAllSandboxAccounts -> {
                sandboxRepository.closeAll(model.value.sandboxApi!!)
                viewModelScope.launch {
                    effect.emit(Effect.ShowSnackbar("Все аккаунты закрыты."))
                    clearData()
                }
            }

            is Event.ChangeMoneyValue -> {
                model.update {
                    it.copy(moneyValue = event.moneyValue)
                }
            }

            Event.AddMoney -> {
                val value = model.value.moneyValue.toIntOrNull()
                if (value != null && model.value.accountId.isNotEmpty()) {
                    sandboxRepository.addMoney(
                        value = value,
                        sandboxApi = model.value.sandboxApi!!,
                        accountId = model.value.accountId
                    )
                    viewModelScope.launch(Dispatchers.IO) {
                        delay(2.seconds)
                        val portfolio = sandboxRepository.getPortfolio(
                            model.value.sandboxApi!!,
                            accountId = model.value.accountId
                        )
                        delay(2.seconds)
                        withContext(Dispatchers.Main) {
                            model.update {
                                it.copy(
                                    portfolio = portfolio,
                                    moneyValue = ""
                                )
                            }
                        }
                    }
                } else {
                    var msg = ""
                    if (value == null)
                        msg += "Неверно введена сумма пополнения.\n"
                    if (model.value.accountId.isEmpty())
                        msg += "Нет активного ID аккаунта."
                    viewModelScope.launch {
                        effect.emit(Effect.ShowSnackbar(msg))
                    }
                }
            }

            is Event.SearchInstrument -> {
                viewModelScope.launch {
                    val instrumentsBy: List<InstrumentShort> = sandboxRepository.getInstrumentsBy(
                        sandboxApi = model.value.sandboxApi!!,
                        id = event.id
                    )
                    val resultMutableList: MutableList<InstrumentShort> = mutableListOf()
                    val sandboxApi = model.value.sandboxApi
                    if (sandboxApi != null) {
                        instrumentsBy.forEach {
                            if (it.apiTradeAvailableFlag) {
                                resultMutableList.add(it)
                            }
                        }
                    }

                    withContext(Dispatchers.Main) {
                        model.update {
                            it.copy(instrumentsBy = resultMutableList.toList())
                        }
                    }
                }
            }

            is Event.BuyLot -> {

                val lots = event.lot.toIntOrNull()
                val sandboxApi = model.value.sandboxApi

                if (lots != null && sandboxApi != null)
                    viewModelScope.launch {
                        sandboxRepository.buyWithLots(
                            sandboxApi = sandboxApi,
                            lots = lots,
                            accountId = model.value.accountId,
                            figi = model.value.selectedFigi
                        )
                    }

                updatePortfolio()
            }

            is Event.BuyWithMoney -> {
                viewModelScope.launch {
                    sandboxRepository.buyWithMoney(
                        sandboxApi = model.value.sandboxApi!!,
                        money = event.money,
                        accountId = model.value.accountId,
                        figi = model.value.selectedFigi
                    )
                }
            }

            is Event.SellLot -> {

            }

            is Event.SellWithMoney -> {

            }

            is Event.SelectInstrument -> {
                model.update {
                    it.copy(selectedFigi = event.figi)
                }
            }

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

            is Event.StartTrading -> {
                model.update {
                    it.copy(
                        isStartTrading = event.isStartTrading
                    )
                }

                var msg = ""
                if (model.value.isStartTrading)
                    msg += "Торги начаты."
                else
                    msg += "Торги остановлены."
                viewModelScope.launch {
                    effect.emit(Effect.ShowSnackbar(msg))
                }

                runTrading()
            }
        }
    }

    private suspend fun clearData() {
        sandboxRepository.saveSandboxAccountId(accountId = "")

        withContext(Dispatchers.Main) {
            model.update {
                it.copy(
                    accountId = "",
                    portfolio = null
                )
            }
        }
    }

    fun getInstrumentNameByFigi(figi: String): String {
        val sandboxApi = model.value.sandboxApi
        return if (sandboxApi != null) {
            sandboxApi.instrumentsService.getInstrumentByFigiSync(figi).name
        } else "Неопределено"
    }

    private fun updatePortfolio() {
        val sandboxApi = model.value.sandboxApi
        if (sandboxApi != null) {
            val portfolio =
                sandboxRepository.getPortfolio(sandboxApi, model.value.accountId)
            val positions: MutableList<Position> = portfolio.positions
            model.update {
                it.copy(
                    portfolio = portfolio,
                    positions = positions
                )
            }
        }
    }

    fun getInstrument(figi: String): Instrument? {
        val sandboxApi = model.value.sandboxApi
        return sandboxApi?.instrumentsService?.getInstrumentByFigiSync(figi)
    }

    fun getCurrentPrice(runPriceStream: Boolean) {
        if (runPriceStream) {
            val sandboxApi = model.value.sandboxApi

            viewModelScope.launch(Dispatchers.IO) {
                while (true) {
                    delay(1.seconds)
                    if (sandboxApi != null) {
                        val portfolio: Portfolio = sandboxRepository.getPortfolio(
                            sandboxApi = sandboxApi,
                            accountId = model.value.accountId
                        )

                        val positions = portfolio.positions
                        withContext(Dispatchers.Main) {
                            model.update {
                                it.copy(
                                    positions = positions
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun runTrading() {

        val tradingModels = model.value.tradingModels.toList()
        val sandboxApi = model.value.sandboxApi

        if (sandboxApi != null) {
            val startPricesMap = mutableMapOf<TradingModel, Money>()
            val isSoldMap = mutableMapOf<TradingModel, Boolean>()

            var portfolio: Portfolio = sandboxRepository.getPortfolio(
                sandboxApi = sandboxApi,
                accountId = model.value.accountId
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
                    portfolio = sandboxRepository.getPortfolio(
                        sandboxApi = sandboxApi,
                        accountId = model.value.accountId
                    )
                    positions = portfolio.positions
                    val jobs = mutableListOf<Job>()

                    tradingModels.forEachIndexed { _, tradingModel ->
                        positions.forEachIndexed { _, position ->
                            if (tradingModel.figi == position.figi) {
                                val job = viewModelScope.launch(supervisorDefaultTradingContext) {
                                    val instrument = getInstrument(figi = tradingModel.figi)
                                    val isSold = isSoldMap.getOrDefault(tradingModel, false)
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
                                                    withContext(supervisorIOTradingContext) {

                                                    }

                                                    operation = "BUY"
                                                    println("-->$operation<--\n=====================================")

                                                    isSoldMap[tradingModel] = false

                                                    //Change old price
                                                    startPricesMap[tradingModel] =
                                                        Money.fromResponse(moneyValue)
                                                }
                                            } else {
                                                //HOLD
                                                operation = "HOLD"
                                                println("-->$operation<--\n=====================================")
                                            }
                                        }
                                        if (percentChange > 0) {
                                            if (percentChange >= increasePercent) {
                                                //SELL
                                                if (!isSold) {
                                                    withContext(supervisorIOTradingContext) {
                                                        sandboxRepository.sellWithLots(
                                                            sandboxApi = sandboxApi,
                                                            lots = tradingModel.countLots,
                                                            accountId = model.value.accountId,
                                                            figi = tradingModel.figi
                                                        )
                                                    }
                                                    operation = "SELL"
                                                    isSoldMap[tradingModel] = true
                                                    println("-->$operation<--\n=====================================")

                                                    //Change old price
                                                    startPricesMap[tradingModel] =
                                                        Money.fromResponse(moneyValue)
                                                }
                                            } else {
                                                //HOLD
                                                operation = "HOLD"
                                                println("-->$operation<--\n=====================================")
                                            }
                                        }
                                        if (percentChange == 0.0) {
                                            //HOLD
                                            operation = "HOLD"
                                            println("-->$operation<--\n=====================================")
                                        }

                                        withContext(supervisorIOTradingContext) {
                                            logRepository.write(
                                                LogSandbox(
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
                    jobs.forEach { it.join() }
                }
            }

            if (!model.value.isStartTrading) {
                job.cancel()
            }
        }
    }
}