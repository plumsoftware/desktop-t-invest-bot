package ru.plumsoftware.ui.presentation.screens.sandbox

import kotlinx.coroutines.Dispatchers
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

                        model.update {
                            it.copy(
                                accountId = accountId,
                                sandboxApi = sandboxApi
                            )
                        }
                    } else {
                        model.update {
                            it.copy(
                                accountId = lastSandboxAccountId,
                                sandboxApi = sandboxApi
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
        }
    }

    private suspend fun clearData() {
        sandboxRepository.saveSandboxAccountId(accountId = "")

        withContext(Dispatchers.Main) {
            model.update {
                it.copy(
                    accountId = "",
                )
            }
        }
    }
}