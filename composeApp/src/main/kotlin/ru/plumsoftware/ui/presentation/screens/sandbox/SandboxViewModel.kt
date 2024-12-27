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
import ru.tinkoff.piapi.core.InvestApi
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

                    val sandboxApi: InvestApi =
                        sandboxRepository.getSandboxApi(settings.apiTokens.sandboxToken)

                    if (lastSandboxAccountId.isEmpty()) {
                        val accountId: String =
                            sandboxRepository.sandboxService(sandboxApi = sandboxApi, figi = "")
                        sandboxRepository.saveSandboxAccountId(accountId = accountId)
                        val portfolio = sandboxRepository.getPortfolio(sandboxApi, accountId)

                        model.update {
                            it.copy(
                                accountId = accountId,
                                sandboxApi = sandboxApi,
                                portfolio = portfolio
                            )
                        }
                    } else {
                        val portfolio =
                            sandboxRepository.getPortfolio(sandboxApi, lastSandboxAccountId)
                        model.update {
                            it.copy(
                                accountId = lastSandboxAccountId,
                                sandboxApi = sandboxApi,
                                portfolio = portfolio
                            )
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
}