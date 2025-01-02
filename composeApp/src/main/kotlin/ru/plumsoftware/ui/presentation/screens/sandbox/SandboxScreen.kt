package ru.plumsoftware.ui.presentation.screens.sandbox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import ru.plumsoftware.ui.components.TopBar
import ru.plumsoftware.ui.presentation.screens.sandbox.model.Effect
import ru.plumsoftware.ui.presentation.screens.sandbox.model.Event
import ru.plumsoftware.ui.theme.Space
import ru.plumsoftware.ui.components.sandbox_tabs.PortfolioTab
import ru.plumsoftware.ui.components.sandbox_tabs.InstrumentsTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SandboxScreen(
    navigator: Navigator,
    settingsRepository: SettingsRepository,
    sandboxRepository: SandboxRepository
) {

    val viewModel = viewModel(modelClass = SandboxViewModel::class) {
        SandboxViewModel(
            settingsRepository = settingsRepository,
            sandboxRepository = sandboxRepository
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
            var selectedTabIndex by remember { mutableIntStateOf(0) }

            TabRow(selectedTabIndex = selectedTabIndex, modifier = Modifier.fillMaxWidth()) {
                Tab(
                    selected = selectedTabIndex == 0,
                    text = {
                        Text(
                            text = "Счёт и портфолио",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    onClick = {
                        selectedTabIndex = 0
                    },
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    text = {
                        Text(text = "Инструменты", style = MaterialTheme.typography.headlineSmall)
                    },
                    onClick = {
                        selectedTabIndex = 1
                    }
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    text = {
                        Text(text = "Торги", style = MaterialTheme.typography.headlineSmall)
                    },
                    onClick = {
                        selectedTabIndex = 2
                    }
                )
            }

            if (selectedTabIndex == 0) {
                PortfolioTab(model = model, onEvent = viewModel::onEvent)
            }
            if (selectedTabIndex == 1) {
                InstrumentsTab(model = model, onEvent = viewModel::onEvent)
            }
        }
    }
}