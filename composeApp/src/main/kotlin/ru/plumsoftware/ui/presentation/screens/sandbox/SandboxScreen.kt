package ru.plumsoftware.ui.presentation.screens.sandbox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModel
import ru.plumsoftware.core.brokerage.sandbox.repository.SandboxRepository
import ru.plumsoftware.core.settings.repository.SettingsRepository
import ru.plumsoftware.ui.components.SecondaryButton
import ru.plumsoftware.ui.components.TopBar
import ru.plumsoftware.ui.presentation.screens.sandbox.model.Effect
import ru.plumsoftware.ui.presentation.screens.sandbox.model.Event
import ru.plumsoftware.ui.theme.Space

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
        Row(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
                .padding(horizontal = Space.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                space = Space.medium,
                alignment = Alignment.Start
            )
        ) {
            Text(
                text = "Идентификатор вашего аккаунта: ${model.value.accountId}",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1.0f)
            )
            SecondaryButton(
                text = "Закрыть все аккаунты",
                onClick = {
                    viewModel.onEvent(Event.CloseAllSandboxAccounts)
                }
            )
        }
    }
}