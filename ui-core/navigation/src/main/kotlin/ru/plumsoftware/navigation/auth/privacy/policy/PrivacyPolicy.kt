package ru.plumsoftware.navigation.auth.privacy.policy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import moe.tlaster.precompose.navigation.Navigator
import org.jetbrains.compose.resources.stringResource
import ru.plumsoftware.components.bar.top.PrimaryTopAppBar
import ru.plumsoftware.components.button.PrimaryButton
import ru.plumsoftware.navigation.auth.privacy.policy.model.Effect
import ru.plumsoftware.navigation.auth.privacy.policy.model.Event
import ru.plumsoftware.theme.Space
import ru.plumsoftware.ui.core.resources.Res
import ru.plumsoftware.ui.core.resources.privacy_policy
import ru.plumsoftware.ui.core.resources.privacy_policy_title
import ru.plumsoftware.ui.core.resources.read

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicy(navigator: Navigator) {
    val viewModel = viewModel { PrivacyPolicyViewModel() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect {
            when (it) {
                Effect.Read -> {
                    navigator.goBack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            PrimaryTopAppBar(
                title = stringResource(Res.string.privacy_policy_title)
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(all = Space.small),
            verticalArrangement = Arrangement.spacedBy(
                space = Space.medium,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = stringResource(Res.string.privacy_policy),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start
                )
            }
            item {
                PrimaryButton(
                    text = stringResource(Res.string.read),
                    enabled = true,
                    isLoading = true,
                    onClick = {
                        viewModel.onEvent(Event.Read)
                    }
                )
            }
        }
    }
}