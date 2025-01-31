package ru.plumsoftware.navigation.auth.variant

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.rememberNavigator
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ru.plumsoftware.components.bar.top.PrimaryTopAppBar
import ru.plumsoftware.components.button.PrimaryButton
import ru.plumsoftware.components.button.PrimaryOutlinedButton
import ru.plumsoftware.navigation.Route
import ru.plumsoftware.navigation.auth.variant.model.Effect
import ru.plumsoftware.theme.AppTheme
import ru.plumsoftware.theme.Space
import ru.plumsoftware.theme.disabled
import ru.plumsoftware.ui.core.resources.Res
import ru.plumsoftware.ui.core.resources.app_name
import ru.plumsoftware.ui.core.resources.auth
import ru.plumsoftware.ui.core.resources.logo
import ru.plumsoftware.ui.core.resources.or
import ru.plumsoftware.ui.core.resources.register

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthVariant(onlyAuth: Boolean = false, navigator: Navigator) {

    val viewModel = viewModel { AuthVariantViewModel() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect {
            when (it) {
                Effect.Auth -> {
                    navigator.navigate(route = Route.Auth.AUTHENTICATION)
                }
                Effect.Register -> {
                    navigator.navigate(route = Route.Auth.REGISTER)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            PrimaryTopAppBar(
                title = stringResource(Res.string.app_name)
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(all = Space.small),
            verticalArrangement = Arrangement.spacedBy(
                space = Space.large,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .clip(shape = MaterialTheme.shapes.medium)
                    .size(width = 150.dp, height = 150.dp),
                painter = painterResource(Res.drawable.logo),
                contentDescription = stringResource(Res.string.app_name)
            )

            Spacer(modifier = Modifier.height(height = Space.large))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(
                    space = Space.small,
                    alignment = Alignment.CenterVertically
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!onlyAuth) {
                    PrimaryButton(
                        text = stringResource(Res.string.register),
                        enabled = true,
                        isLoading = false,
                        onClick = {
                            viewModel.onEvent(ru.plumsoftware.navigation.auth.variant.model.Event.Register)
                        }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(
                            space = Space.small,
                            alignment = Alignment.CenterHorizontally
                        )
                    ) {
                        Divider(
                            modifier = Modifier
                                .weight(1.0f)
                                .padding(start = Space.medium)
                                .clip(shape = MaterialTheme.shapes.small),
                            thickness = 2.dp,
                            color = LocalContentColor.current.disabled()
                        )
                        Text(
                            text = stringResource(Res.string.or),
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onBackground.disabled())
                        )
                        Divider(
                            modifier = Modifier
                                .weight(1.0f)
                                .padding(end = Space.medium)
                                .clip(shape = MaterialTheme.shapes.small),
                            thickness = 2.dp,
                            color = LocalContentColor.current.disabled()
                        )
                    }
                }
                PrimaryOutlinedButton(
                    text = stringResource(Res.string.auth),
                    isLoading = false,
                    enabled = true,
                    onClick = {
                        viewModel.onEvent(ru.plumsoftware.navigation.auth.variant.model.Event.Auth)
                    }
                )
            }
        }
    }
}

@Composable
@Preview
private fun AuthVariantPreview() {
    AppTheme(useDarkTheme = true) {
        AuthVariant(navigator = rememberNavigator())
    }
}
