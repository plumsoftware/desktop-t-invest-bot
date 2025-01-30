package ru.plumsoftware.navigation.confirm.number

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import moe.tlaster.precompose.navigation.Navigator
import org.jetbrains.compose.resources.stringResource
import ru.plumsoftware.PlatformSpecific
import ru.plumsoftware.components.bar.top.PrimaryTopAppBar
import ru.plumsoftware.components.button.PrimaryButton
import ru.plumsoftware.components.text.input.PrimaryTextInput
import ru.plumsoftware.navigation.confirm.number.model.Action
import ru.plumsoftware.navigation.confirm.number.model.Effect
import ru.plumsoftware.navigation.confirm.number.model.Event
import ru.plumsoftware.theme.Space
import ru.plumsoftware.ui.core.resources.Res
import ru.plumsoftware.ui.core.resources.auth
import ru.plumsoftware.ui.core.resources.confirm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmNumber(navigator: Navigator, platformSpecific: PlatformSpecific) {
    val viewModel = viewModel { ConfirmNumberViewModel(platformSpecific = platformSpecific) }

    LaunchedEffect(Unit) {
        viewModel.onAction(Action.SendCode)
        viewModel.effect.collect {
            when (it) {
                Effect.Back -> {
                    navigator.goBack()
                }

                Effect.Next -> {
                    //TODO()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            PrimaryTopAppBar(
                title = stringResource(Res.string.auth),
                onBack = {
                    viewModel.onEvent(Event.Back)
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(all = Space.small),
            verticalArrangement = Arrangement.spacedBy(
                space = Space.medium,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PrimaryTextInput(
                startText = viewModel.state.value.code,
                enabled = true,
                onValueChange = {
                    viewModel.onEvent(Event.OnCodeChanged(code = it))
                }
            )
            PrimaryButton(
                text = stringResource(Res.string.confirm),
                enabled = true,
                isLoading = false,
                onClick = {
                    viewModel.onEvent(Event.Confirm)
                }
            )
        }
    }
}
