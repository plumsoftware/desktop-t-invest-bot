package ru.plumsoftware.ui.presentation.screens.market

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModel
import ru.plumsoftware.core.brokerage.market.MarketRepository
import ru.plumsoftware.core.settings.repository.SettingsRepository
import ru.plumsoftware.log.repository.LogRepository
import ru.plumsoftware.ui.components.BackButton
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
            CenterAlignedTopAppBar(
                title = {
                    Column(
                        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                        verticalArrangement = Arrangement.spacedBy(
                            space = Space.small,
                            alignment = Alignment.CenterVertically
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        with(model.value.portfolio?.totalAmountPortfolio) {
                            Text(
                                text = "${this?.value} ${this?.currency}",
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                        with(model.value.stopTradingTotalAmountPortfolio) {
                            val value = if (this?.value == null) "-" else this.value.toString()
                            val currency = if (this?.currency == null) "-" else this.currency
                            val totalPortfolio = model.value.portfolio?.totalAmountPortfolio
                            val stopTotalPortfolio = model.value.stopTradingTotalAmountPortfolio

                            val difference =
                                model.value.stopTradingTotalAmountPortfolio?.value?.minus(
                                    model.value.portfolio?.totalAmountPortfolio?.value!!
                                )
                            val color = if (totalPortfolio != null && stopTotalPortfolio != null) {
                                if (totalPortfolio.value > stopTotalPortfolio.value) Color.Red else if (totalPortfolio.value < stopTotalPortfolio.value) Color.Green else MaterialTheme.colorScheme.onBackground
                            } else {
                                MaterialTheme.colorScheme.onBackground
                            }
                            val result = difference?.toString() ?: "-"

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(
                                    space = Space.small,
                                    alignment = Alignment.CenterHorizontally
                                )
                            ) {
                                Text(
                                    text = "$value $currency",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Text(
                                    text = "($result)",
                                    style = MaterialTheme.typography.headlineSmall.copy(color = color)
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(
                            space = Space.small,
                            alignment = Alignment.Start
                        )
                    ) {
                        BackButton(onClick = { viewModel.onEvent(Event.Back) })
                        Text(
                            text = "Маркет",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }

            )
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