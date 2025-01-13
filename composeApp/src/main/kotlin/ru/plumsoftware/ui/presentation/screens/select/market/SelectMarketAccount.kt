package ru.plumsoftware.ui.presentation.screens.select.market

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModel
import ru.plumsoftware.core.brokerage.market.MarketRepository
import ru.plumsoftware.core.settings.repository.SettingsRepositoryImpl
import ru.plumsoftware.ui.components.PrimaryTextButton
import ru.plumsoftware.ui.components.TopBar
import ru.plumsoftware.ui.presentation.screens.select.market.model.Effect
import ru.plumsoftware.ui.presentation.screens.select.market.model.Event
import ru.plumsoftware.ui.root.DesktopRouting
import ru.plumsoftware.ui.theme.Space

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectMarketAccount(
    navigator: Navigator,
    marketRepository: MarketRepository,
    settingsRepository: SettingsRepositoryImpl
) {

    val viewModel = viewModel(modelClass = SelectMarketAccountViewModel::class) {
        SelectMarketAccountViewModel(
            settingsRepository = settingsRepository,
            marketRepository = marketRepository
        )
    }
    val model = viewModel.model.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.onEvent(Event.Init)
        viewModel.effect.collect { effect ->
            when (effect) {
                Effect.Back -> {
                    navigator.goBack()
                }

                Effect.Next -> {
                    navigator.navigate(route = DesktopRouting.market)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                title = "Выбрать аккаунт",
                onBack = {
                    viewModel.onEvent(Event.Close)
                }
            )
        }
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(
                space = Space.medium,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(
                    horizontal = Space.medium,
                    vertical = Space.medium
                )
                .padding(it)
        ) {
            itemsIndexed(model.value.accounts) { _, item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    PrimaryTextButton(
                        text = item.id,
                        onClick = {
                            viewModel.onEvent(Event.SelectAccount(account = item))
                        }
                    )
                }
            }
        }
    }
}