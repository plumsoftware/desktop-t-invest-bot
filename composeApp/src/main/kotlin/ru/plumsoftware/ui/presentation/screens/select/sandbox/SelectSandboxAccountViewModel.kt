package ru.plumsoftware.ui.presentation.screens.select.sandbox

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import ru.plumsoftware.core.brokerage.sandbox.repository.SandboxRepository
import ru.plumsoftware.core.settings.repository.SettingsRepository
import ru.plumsoftware.ui.presentation.screens.select.sandbox.model.Effect
import ru.plumsoftware.ui.presentation.screens.select.sandbox.model.Event
import ru.plumsoftware.ui.presentation.screens.select.sandbox.model.Model

class SelectSandboxAccountViewModel(
    private val settingsRepository: SettingsRepository,
    private val sandboxRepository: SandboxRepository
) : ViewModel() {
    val model = MutableStateFlow(Model())
    val effect = MutableSharedFlow<Effect>()

    private val supervisorIOCoroutineContext = Dispatchers.IO + SupervisorJob()

    fun onEvent(event: Event) {
        when (event) {
            is Event.CloseAccount -> {
                viewModelScope.launch(supervisorIOCoroutineContext) {
                    if (model.value.investApi != null)
                        sandboxRepository.closeAccount(
                            sandboxApi = model.value.investApi!!,
                            accountId = event.account.id
                        )
                    val accounts =
                        sandboxRepository.getSandboxAccounts(sandboxApi = model.value.investApi!!)

                    val lastSandboxAccountId: String = sandboxRepository.getLastSandboxAccountId()
                    if (lastSandboxAccountId == event.account.id)
                        sandboxRepository.saveSandboxAccountId(accountId = "")

                    withContext(Dispatchers.Main) {
                        model.update {
                            it.copy(
                                accounts = accounts
                            )
                        }
                    }
                }
            }

            Event.OpenNew -> {
                if (model.value.accounts.size <= 10) {
                    viewModelScope.launch(supervisorIOCoroutineContext) {
                        sandboxRepository.sandboxService(
                            sandboxApi = model.value.investApi!!,
                            figi = "",
                        )
                        val accounts =
                            sandboxRepository.getSandboxAccounts(sandboxApi = model.value.investApi!!)
                        model.update {
                            it.copy(
                                accounts = accounts
                            )
                        }
                    }
                }
            }

            is Event.SelectAccount -> {
                viewModelScope.launch(supervisorIOCoroutineContext) {
                    sandboxRepository.saveSandboxAccountId(
                        accountId = event.account.id
                    )
                }
                next()
            }

            Event.Init -> init()


            Event.Close -> {
                viewModelScope.launch {
                    effect.emit(Effect.Back)
                    destroyApi()
                }
            }
        }
    }

    private fun next() {
        viewModelScope.launch {
            effect.emit(Effect.OpenSandbox)
            destroyApi()
        }
    }

    private fun destroyApi() {
        model.value.investApi?.destroy(3)
    }

    private fun init() {
        viewModelScope.launch(supervisorIOCoroutineContext) {
            val sandboxApi =
                sandboxRepository.getSandboxApi(token = settingsRepository.getSettings().apiTokens.sandboxToken)
            var accounts = sandboxRepository.getSandboxAccounts(sandboxApi = sandboxApi)

            if (accounts.isEmpty()) {
                sandboxRepository.sandboxService(sandboxApi = sandboxApi, figi = "")
                accounts = sandboxRepository.getSandboxAccounts(sandboxApi = sandboxApi)
            }

            withContext(viewModelScope.coroutineContext) {
                model.update {
                    it.copy(
                        investApi = sandboxApi,
                        accounts = accounts
                    )
                }
            }
        }
    }
}