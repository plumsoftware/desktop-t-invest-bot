package ru.plumsoftware.ui.presentation.screens.select.sandbox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModel
import ru.plumsoftware.core.brokerage.sandbox.repository.SandboxRepository
import ru.plumsoftware.core.settings.repository.SettingsRepository
import ru.plumsoftware.ui.components.PrimaryTextButton
import ru.plumsoftware.ui.components.SecondaryButton
import ru.plumsoftware.ui.components.TopBar
import ru.plumsoftware.ui.presentation.screens.select.sandbox.model.Effect
import ru.plumsoftware.ui.presentation.screens.select.sandbox.model.Event
import ru.plumsoftware.ui.root.DesktopRouting
import ru.plumsoftware.ui.theme.Space

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectSandboxAccount(
    navigator: Navigator,
    sandboxRepository: SandboxRepository,
    settingsRepository: SettingsRepository,
) {

    val viewModel = viewModel(modelClass = SelectSandboxAccountViewModel::class) {
        SelectSandboxAccountViewModel(
            settingsRepository = settingsRepository,
            sandboxRepository = sandboxRepository
        )
    }
    val model = viewModel.model.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.onEvent(Event.Init)
        viewModel.effect.collect { effect ->
            when (effect) {
                Effect.OpenSandbox -> {
                    navigator.navigate(route = DesktopRouting.sandbox)
                }

                Effect.Back -> {
                    navigator.goBack()
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
            item {
                SecondaryButton(
                    text = "Открыть новый",
                    onClick = {
                        viewModel.onEvent(Event.OpenNew)
                    }
                )
            }
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
                    Spacer(modifier = Modifier.weight(1.0f))
                    IconButton(
                        onClick = {
                            viewModel.onEvent(Event.CloseAccount(account = item))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Закрыть аккаунт ${item.name}"
                        )
                    }
                }
            }
        }
    }
}