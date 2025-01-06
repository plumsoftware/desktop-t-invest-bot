package ru.plumsoftware.ui.presentation.screens.sandbox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModel
import ru.plumsoftware.core.brokerage.sandbox.repository.SandboxRepository
import ru.plumsoftware.core.settings.repository.SettingsRepository
import ru.plumsoftware.log.repository.LogRepository
import ru.plumsoftware.ui.components.TopBar
import ru.plumsoftware.ui.presentation.screens.sandbox.model.Effect
import ru.plumsoftware.ui.presentation.screens.sandbox.model.Event
import ru.plumsoftware.ui.theme.Space
import ru.plumsoftware.ui.components.sandbox_tabs.PortfolioTab
import ru.plumsoftware.ui.components.sandbox_tabs.InstrumentsTab
import ru.plumsoftware.ui.components.sandbox_tabs.MarketTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SandboxScreen(
    navigator: Navigator,
    settingsRepository: SettingsRepository,
    sandboxRepository: SandboxRepository,
    logRepository: LogRepository
) {

    val viewModel = viewModel(modelClass = SandboxViewModel::class) {
        SandboxViewModel(
            settingsRepository = settingsRepository,
            sandboxRepository = sandboxRepository,
            logRepository = logRepository
        )
    }
    val model = viewModel.model.collectAsState()
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(key1 = selectedTabIndex) {
        if (selectedTabIndex == 0 || selectedTabIndex == 2) {
            viewModel.onEvent(Event.Init)
            viewModel.getCurrentPrice(selectedTabIndex == 2)
        }

    }

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

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = model.value.snackbarHostState)
        },
        topBar = {
            TopBar(title = "Песочница", onBack = { viewModel.onEvent(Event.Back) })
        },
        bottomBar = {
            BottomAppBar(
                icons = {
                    Box(
                        modifier = Modifier.weight(1.0f)
                    ) {
                        IconButton(
                            modifier = Modifier.align(Alignment.Center),
                            onClick = {
                                selectedTabIndex = 0
                            }
                        ) {
                            Icon(imageVector = Icons.Rounded.Person, contentDescription = "Профиль")
                        }
                    }
                    Box(
                        modifier = Modifier.weight(1.0f)
                    ) {
                        IconButton(
                            modifier = Modifier.align(Alignment.Center),
                            onClick = {
                                selectedTabIndex = 1
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Build,
                                contentDescription = "Инструменты"
                            )
                        }
                    }
                    Box(
                        modifier = Modifier.weight(1.0f)
                    ) {
                        IconButton(
                            modifier = Modifier.align(Alignment.Center),
                            onClick = {
                                selectedTabIndex = 2
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PlayArrow,
                                contentDescription = "Торги в песочнице"
                            )
                        }
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
            if (selectedTabIndex == 0) {
                PortfolioTab(
                    model = model,
                    onEvent = viewModel::onEvent,
                    getName = viewModel::getInstrumentNameByFigi
                )
            }
            if (selectedTabIndex == 1) {
                InstrumentsTab(model = model, onEvent = viewModel::onEvent)
            }
            if (selectedTabIndex == 2) {
                MarketTab(
                    model = model,
                    onEvent = viewModel::onEvent,
                    getInstrument = viewModel::getInstrument,
                )
            }
        }
    }
}