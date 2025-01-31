package ru.plumsoftware.ui.presentation.screens.select.market

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import ru.plumsoftware.core.brokerage.market.MarketRepository
import ru.plumsoftware.core.settings.repository.SettingsRepository
import ru.plumsoftware.ui.presentation.screens.select.market.model.Effect
import ru.plumsoftware.ui.presentation.screens.select.market.model.Event
import ru.plumsoftware.ui.presentation.screens.select.market.model.Model

class SelectMarketAccountViewModel(
    private val settingsRepository: SettingsRepository,
    private val marketRepository: MarketRepository
) : ViewModel() {

    val model = MutableStateFlow(Model())
    val effect = MutableSharedFlow<Effect>()

    private val supervisorIOCoroutineContext = Dispatchers.IO + SupervisorJob()

    fun onEvent(event: Event) {
        when (event) {
            Event.Close -> viewModelScope.launch {
                effect.emit(Effect.Back)
                destroyApi()
            }

            Event.Init -> init()
            is Event.SelectAccount -> {
                viewModelScope.launch(supervisorIOCoroutineContext) {
                    marketRepository.saveMarketAccountId(
                        accountId = event.account.id
                    )
                }
                next()
            }
        }
    }

    private fun next() {
        viewModelScope.launch {
            effect.emit(Effect.Next)
            destroyApi()
        }
    }

    private fun destroyApi() {
        model.value.investApi?.destroy(3)
    }

    private fun init() {
        viewModelScope.launch(supervisorIOCoroutineContext) {
            val token = settingsRepository.getSettings().apiTokens.token
            val api =
                marketRepository.getMarketApi(token = token)
            val accounts = marketRepository.getMarketAccounts(api = api)

            withContext(viewModelScope.coroutineContext) {
                model.update {
                    it.copy(
                        investApi = api,
                        accounts = accounts
                    )
                }
            }
        }
    }
}