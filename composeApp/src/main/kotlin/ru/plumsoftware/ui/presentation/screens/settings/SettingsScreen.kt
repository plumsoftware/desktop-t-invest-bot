package ru.plumsoftware.ui.presentation.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModel
import ru.plumsoftware.core.settings.repository.SettingsRepository
import ru.plumsoftware.ui.components.BackButton
import ru.plumsoftware.ui.components.PrimaryButton
import ru.plumsoftware.ui.presentation.screens.settings.model.Effect
import ru.plumsoftware.ui.presentation.screens.settings.model.Event
import ru.plumsoftware.ui.theme.Space

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(settingsRepository: SettingsRepository, navigator: Navigator) {

    val settingsViewModel = viewModel(modelClass = SettingsViewModel::class) {
        SettingsViewModel(settingsRepository = settingsRepository)
    }
    val model = settingsViewModel.model.collectAsState()

    LaunchedEffect(key1 = Unit) {
        settingsViewModel.onEvent(Event.LoadSettings)
        settingsViewModel.effect.collect { effect ->
            when (effect) {
                Effect.Back -> navigator.goBack()
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Настройки",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    BackButton(
                        onClick = {
                            settingsViewModel.onEvent(Event.Back)
                        }
                    )
                }
            )
        },
        floatingActionButton = {
            PrimaryButton(
                text = "Сохранить",
                onClick = {
                    settingsViewModel.onEvent(Event.SaveSettings)
                }
            )
        }
    ) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.spacedBy(
                space = Space.large,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxSize().padding(paddingValues)
                .padding(horizontal = Space.medium)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    space = Space.small,
                    alignment = Alignment.CenterVertically
                ),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = model.value.token,
                    shape = MaterialTheme.shapes.medium,
                    colors = TextFieldDefaults.outlinedTextFieldColors(),
                    textStyle = MaterialTheme.typography.headlineMedium,
                    label = {
                        Text(text = "Токен", style = MaterialTheme.typography.headlineSmall)
                    },
                    placeholder = {
                        Text(text = "Токен", style = MaterialTheme.typography.headlineSmall)
                    },
                    onValueChange = {
                        settingsViewModel.onEvent(Event.ChangeToken(token = it))
                    }
                )
                OutlinedTextField(
                    value = model.value.sandboxToken,
                    shape = MaterialTheme.shapes.medium,
                    colors = TextFieldDefaults.outlinedTextFieldColors(),
                    textStyle = MaterialTheme.typography.headlineMedium,
                    label = {
                        Text(
                            text = "Токен песочницы",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    placeholder = {
                        Text(
                            text = "Токен песочницы",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    onValueChange = {
                        settingsViewModel.onEvent(Event.ChangeSandboxToken(sandboxToken = it))
                    }
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    space = Space.medium,
                    alignment = Alignment.CenterHorizontally
                )
            ) {
                Text(text = "Тёмная тема", style = MaterialTheme.typography.headlineSmall)
                Checkbox(
                    checked = model.value.isDark,
                    onCheckedChange = { settingsViewModel.onEvent(Event.ChangeIsDark(isDark = it)) })
            }
        }
    }
}