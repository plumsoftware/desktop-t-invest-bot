package ru.plumsoftware.ui.presentation.screens.market

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModel
import ru.plumsoftware.core.brokerage.market.MarketRepository
import ru.plumsoftware.core.settings.repository.SettingsRepository
import ru.plumsoftware.log.repository.LogRepository
import ru.plumsoftware.ui.components.TopBar
import ru.plumsoftware.ui.components.tabs.MarketTab
import ru.plumsoftware.ui.presentation.screens.market.model.Effect
import ru.plumsoftware.ui.presentation.screens.market.model.Event
import ru.plumsoftware.ui.theme.Space

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketScreen(
    navigator: Navigator,
    settingsRepository: SettingsRepository,
    marketRepository: MarketRepository,
    logRepository: LogRepository
) {
    val viewModel = viewModel(modelClass = MarketViewModel::class) {
        MarketViewModel(
            settingsRepository = settingsRepository,
            marketRepository = marketRepository,
            logRepository = logRepository
        )
    }
    val model = viewModel.model.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                Effect.Back -> {
                    navigator.goBack()
                }

                is Effect.ShowSnackbar -> {
                    model.value.snackbarHostState.showSnackbar(effect.msg)
                }
            }
        }
    }
    LaunchedEffect(key1 = Unit) {
        viewModel.onEvent(Event.Init)
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = model.value.snackbarHostState)
        },
        topBar = {
            TopBar(title = "Маркет", onBack = { viewModel.onEvent(Event.Back) })
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(horizontal = Space.large),
            verticalArrangement = Arrangement.spacedBy(
                space = Space.medium,
                alignment = Alignment.Top
            ),
            horizontalAlignment = Alignment.Start
        ) {
            MarketTab(
                positions = model.value.portfolio?.positions,
                getInstrument = viewModel::getInstrumentByFigi,
                onCheckedChange = { figi, value, increase, decrease, checked ->
                    viewModel.onEvent(
                        Event.AddToTrading(
                            figi = figi,
                            countLots = value,
                            increase = increase,
                            decrease = decrease,
                            isTrading = checked
                        )
                    )
                },
                onStartTrading = { isStartTrading ->
                    viewModel.onEvent(Event.StartTrading(isStartTrading = isStartTrading))
                }
            )
        }
    }
}