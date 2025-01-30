package ru.plumsoftware.navigation.auth.auth

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.rememberNavigator
import org.jetbrains.compose.resources.stringResource
import ru.plumsoftware.components.bar.top.PrimaryTopAppBar
import ru.plumsoftware.components.button.PrimaryButton
import ru.plumsoftware.components.button.PrimaryCheckBox
import ru.plumsoftware.components.button.text.TertiaryTextButton
import ru.plumsoftware.components.text.input.PrimaryTextInput
import ru.plumsoftware.navigation.Route
import ru.plumsoftware.navigation.auth.auth.model.Effect
import ru.plumsoftware.navigation.auth.auth.model.Event
import ru.plumsoftware.theme.AppTheme
import ru.plumsoftware.theme.Space
import ru.plumsoftware.ui.core.resources.Res
import ru.plumsoftware.ui.core.resources.auth
import ru.plumsoftware.ui.core.resources.continu
import ru.plumsoftware.ui.core.resources.enter_password_hint
import ru.plumsoftware.ui.core.resources.enter_phone_hint
import ru.plumsoftware.ui.core.resources.privacy_policy
import ru.plumsoftware.ui.core.resources.privacy_policy_agree

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Auth(navigator: Navigator, needConfirmNumber: Boolean = true) {

    val viewModel = viewModel { AuthViewModel() }
    var readPrivacyPolicy by remember { mutableStateOf(false) }

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

                Effect.PrivacyPolicy -> {
                    navigator.navigate(route = Route.Auth.PRIVACY_POLICY)
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
                startText = viewModel.state.value.phone,
                hint = stringResource(Res.string.enter_phone_hint),
                enabled = true,
                onValueChange = {
                    viewModel.onEvent(Event.OnPhoneChange(phone = it))
                }
            )
            PrimaryTextInput(
                startText = viewModel.state.value.password,
                hint = stringResource(Res.string.enter_password_hint),
                enabled = true,
                onValueChange = {
                    viewModel.onEvent(Event.OnPasswordChange(password = it))
                }
            )
            Spacer(modifier = Modifier.height(height = Space.large))
            PrimaryButton(
                text = stringResource(Res.string.continu),
                enabled = readPrivacyPolicy,
                isLoading = false,
                onClick = {
                    viewModel.onEvent(Event.Next)
                }
            )


            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(space = Space.small),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.wrapContentSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        space = Space.small,
                        alignment = Alignment.CenterHorizontally
                    )
                ) {
                    PrimaryCheckBox {
                        readPrivacyPolicy = it
                    }
                    Text(text = stringResource(Res.string.privacy_policy_agree))
                }
                TertiaryTextButton(
                    text = stringResource(Res.string.privacy_policy),
                    enabled = true,
                    onClick = {
                        viewModel.onEvent(Event.PrivacyPolicy)
                    }
                )
            }
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