package ru.plumsoftware.ui.presentation.screens.sandbox

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import ru.plumsoftware.core.brokerage.sandbox.repository.SandboxRepository
import ru.plumsoftware.core.settings.repository.SettingsRepository
import ru.plumsoftware.ui.presentation.screens.sandbox.model.Effect
import ru.plumsoftware.ui.presentation.screens.sandbox.model.Event
import ru.plumsoftware.ui.presentation.screens.sandbox.model.Model
import ru.tinkoff.piapi.contract.v1.Instrument
import ru.tinkoff.piapi.contract.v1.InstrumentShort
import ru.tinkoff.piapi.core.InvestApi
import ru.tinkoff.piapi.core.models.Portfolio
import ru.tinkoff.piapi.core.models.Position
import kotlin.time.Duration.Companion.seconds

class SandboxViewModel(
    private val sandboxRepository: SandboxRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val effect = MutableSharedFlow<Effect>()
    val model = MutableStateFlow(Model())

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
}