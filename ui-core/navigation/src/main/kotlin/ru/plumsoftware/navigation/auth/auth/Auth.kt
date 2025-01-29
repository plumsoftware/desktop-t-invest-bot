package ru.plumsoftware.navigation.auth.auth

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.rememberNavigator
import org.jetbrains.compose.resources.stringResource
import ru.plumsoftware.components.bar.top.PrimaryTopAppBar
import ru.plumsoftware.components.button.PrimaryButton
import ru.plumsoftware.components.text.input.PrimaryTextInput
import ru.plumsoftware.navigation.Route
import ru.plumsoftware.navigation.auth.auth.model.Effect
import ru.plumsoftware.navigation.auth.auth.model.Event
import ru.plumsoftware.theme.AppTheme
import ru.plumsoftware.theme.Space
import ru.plumsoftware.ui.core.resources.Res
import ru.plumsoftware.ui.core.resources.auth
import ru.plumsoftware.ui.core.resources.continu
import ru.plumsoftware.ui.core.resources.enter_phone_hint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Auth(navigator: Navigator, needConfirmNumber: Boolean = true) {

    val viewModel = viewModel { AuthViewModel() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect {
            when (it) {
                Effect.Back -> {
                    navigator.goBack()
                }

                Effect.Next -> {
                    if (needConfirmNumber)
                        navigator.navigate(route = Route.Auth.CONFIRM_NUMBER)
                    else
                        navigator.navigate(route = Route.Main.HOME)
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
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(
                space = Space.medium,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PrimaryTextInput(
                startText = viewModel.state.value.phone,
                hint = stringResource(Res.string.enter_phone_hint),
                enabled = true,
                onValueChange = {
                    viewModel.onEvent(Event.OnPhoneChange(phone = it))
                }
            )
            PrimaryButton(
                text = stringResource(Res.string.continu),
                enabled = true,
                isLoading = false,
                onClick = {
                    viewModel.onEvent(Event.Next)
                }
            )
        }
    }
}

@Preview
@Composable
private fun AuthPreview() {
    AppTheme {
        Auth(navigator = rememberNavigator())
    }
}