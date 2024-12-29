package ru.plumsoftware.ui.presentation.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModel
import ru.plumsoftware.ui.components.PrimaryButton
import ru.plumsoftware.ui.presentation.screens.main.model.Effect
import ru.plumsoftware.ui.presentation.screens.main.model.Event
import ru.plumsoftware.ui.root.DesktopRouting
import ru.plumsoftware.ui.theme.Space

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navigator: Navigator) {

    val mainViewModel: MainViewModel =
        viewModel(modelClass = MainViewModel::class) {
            MainViewModel()
        }

    LaunchedEffect(key1 = Unit) {
        mainViewModel.effect.collect { effect ->
            when (effect) {
                Effect.OpenBidding -> {
                    navigator.navigate(route = DesktopRouting.bidding)
                }

                Effect.OpenSandbox -> {
                    navigator.navigate(route = DesktopRouting.selectSandboxAccountId)
                }

                Effect.OpenSettings -> {
                    navigator.navigate(route = DesktopRouting.settings)
                }
            }
        }
    }

    Scaffold {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                space = Space.medium,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            PrimaryButton(
                text = "Торговля",
                onClick = {
                    mainViewModel.onEvent(Event.OpenBiddingClick)
                }
            )
            PrimaryButton(
                text = "Песочница",
                onClick = {
                    mainViewModel.onEvent(Event.OpenSandboxClick)
                }
            )
            PrimaryButton(
                text = "Настройки",
                onClick = {
                    mainViewModel.onEvent(Event.OpenSettingsClick)
                }
            )
        }
    }
}