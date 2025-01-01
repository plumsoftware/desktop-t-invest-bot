package ru.plumsoftware.ui.presentation.screens.sandbox

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModel
import ru.plumsoftware.core.brokerage.sandbox.repository.SandboxRepository
import ru.plumsoftware.core.settings.repository.SettingsRepository
import ru.plumsoftware.ui.components.PortfolioComposable
import ru.plumsoftware.ui.components.PrimaryTextButton
import ru.plumsoftware.ui.components.TopBar
import ru.plumsoftware.ui.presentation.screens.sandbox.model.Effect
import ru.plumsoftware.ui.presentation.screens.sandbox.model.Event
import ru.plumsoftware.ui.theme.Space
import ru.plumsoftware.core.brokerage.mappers.fromInstrumentTypeToName
import ru.plumsoftware.ui.components.TertiaryButton
import ru.plumsoftware.ui.theme.disabled

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
                    }
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(
                        space = Space.medium,
                        alignment = Alignment.Top
                    ),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = model.value.accountId,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    PortfolioComposable(portfolio = model.value.portfolio)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        space = Space.medium,
                        alignment = Alignment.Start
                    )
                ) {

                    TextField(
                        value = model.value.moneyValue,
                        shape = MaterialTheme.shapes.medium,
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent
                        ),
                        textStyle = MaterialTheme.typography.headlineSmall,
                        label = {
                            Text(text = "Сумма", style = MaterialTheme.typography.headlineSmall)
                        },
                        onValueChange = { text ->
                            viewModel.onEvent(Event.ChangeMoneyValue(moneyValue = text))
                        },
                        trailingIcon = {
                            Text(text = "₽", style = MaterialTheme.typography.headlineSmall)
                        }
                    )

                    PrimaryTextButton(
                        text = "Пополнить",
                        enabled = model.value.moneyValue.isNotEmpty(),
                        onClick = {
                            viewModel.onEvent(Event.AddMoney)
                        }
                    )
                }
            }
            if (selectedTabIndex == 1) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(
                        space = Space.medium,
                        alignment = Alignment.Top
                    ),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(
                            space = Space.medium,
                            alignment = Alignment.Start
                        )
                    ) {
                        var instrumentId by remember { mutableStateOf("") }
                        TextField(
                            value = instrumentId,
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color.Transparent
                            ),
                            textStyle = MaterialTheme.typography.headlineSmall,
                            label = {
                                Text(
                                    text = "Поиск инструмента",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                            },
                            placeholder = {
                                Text(
                                    text = "Тикер, figi, название, uid",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                            },
                            onValueChange = { text ->
                                instrumentId = text
                            },
                            leadingIcon = {
                                IconButton(
                                    onClick = {
                                        viewModel.onEvent(Event.SearchInstrument(instrumentId))
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Search,
                                        contentDescription = "Поиск инструмента"
                                    )
                                }
                            }
                        )
                    }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(
                            space = Space.medium,
                            alignment = Alignment.CenterVertically
                        ),
                        horizontalAlignment = Alignment.Start
                    ) {
                        itemsIndexed(model.value.instrumentsBy) { _, item ->
                            var expanded by remember { mutableStateOf(false) }
                            Column(
                                modifier = Modifier
                                    .border(
                                        width = if (expanded) 2.dp else 0.dp,
                                        color = if (expanded) MaterialTheme.colorScheme.onPrimaryContainer else Color.Transparent,
                                        shape = MaterialTheme.shapes.medium
                                    )
                                    .padding(horizontal = Space.large, vertical = Space.medium),
                                verticalArrangement = Arrangement.spacedBy(
                                    space = Space.medium,
                                    alignment = Alignment.CenterVertically
                                ),
                                horizontalAlignment = Alignment.Start
                            ) {
                                var countInPortfolio = 0
                                model.value.portfolio?.positions?.forEachIndexed { _, position ->
                                    if (position.figi == item.figi) {
                                        countInPortfolio = position.quantity.toInt()
                                    }
                                }
                                PrimaryTextButton(
                                    text = "${item.name} | ${fromInstrumentTypeToName(item.instrumentKind)}\nВ портфеле $countInPortfolio",
                                    onClick = {
                                        expanded = !expanded
                                    }
                                )

                                if (expanded) {
                                    var selectedRadioBtn by remember { mutableIntStateOf(0) }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(
                                            space = Space.medium,
                                            alignment = Alignment.Start
                                        )
                                    ) {
                                        RadioButton(
                                            selected = selectedRadioBtn == 0,
                                            onClick = {
                                                selectedRadioBtn = 0
                                            }
                                        )
                                        Text(
                                            text = "₽",
                                            style = MaterialTheme.typography.headlineSmall
                                        )
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(
                                            space = Space.medium,
                                            alignment = Alignment.Start
                                        )
                                    ) {
                                        RadioButton(
                                            selected = selectedRadioBtn == 1,
                                            onClick = {
                                                selectedRadioBtn = 1
                                            }
                                        )
                                        Text(
                                            text = "Лоты",
                                            style = MaterialTheme.typography.headlineSmall
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(
                                            space = Space.large,
                                            alignment = Alignment.CenterHorizontally
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(
                                                space = Space.medium,
                                                alignment = Alignment.CenterHorizontally
                                            ),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            var instrumentBuy by remember { mutableStateOf("") }
                                            var instrumentSell by remember { mutableStateOf("") }

                                            TextField(
                                                value = instrumentBuy,
                                                modifier = Modifier.weight(1.0f),
                                                colors = TextFieldDefaults.textFieldColors(
                                                    containerColor = Color.Transparent
                                                ),
                                                textStyle = MaterialTheme.typography.headlineSmall,
                                                label = {
                                                    Text(
                                                        text = "Купить",
                                                        style = MaterialTheme.typography.headlineSmall
                                                    )
                                                },
                                                onValueChange = { text ->
                                                    instrumentBuy = text
                                                },
                                                trailingIcon = {
                                                    Text(
                                                        text = if (selectedRadioBtn == 0) "₽" else if (selectedRadioBtn == 1) "Лоты" else "",
                                                        style = MaterialTheme.typography.headlineSmall
                                                    )
                                                }
                                            )
                                            TertiaryButton(
                                                text = "Купить",
                                                enabled = instrumentBuy.isNotEmpty(),
                                                onClick = {}
                                            )

                                            Divider(
                                                modifier = Modifier
                                                    .height(20.dp)
                                                    .width(4.dp)
                                                    .clip(shape = MaterialTheme.shapes.small),
                                                thickness = 4.dp,
                                                color = MaterialTheme.colorScheme.onBackground.disabled()
                                            )

                                            TextField(
                                                value = instrumentSell,
                                                modifier = Modifier.weight(1.0f),
                                                colors = TextFieldDefaults.textFieldColors(
                                                    containerColor = Color.Transparent
                                                ),
                                                textStyle = MaterialTheme.typography.headlineSmall,
                                                label = {
                                                    Text(
                                                        text = "Продать",
                                                        style = MaterialTheme.typography.headlineSmall
                                                    )
                                                },
                                                onValueChange = { text ->
                                                    instrumentSell = text
                                                },
                                                trailingIcon = {
                                                    Text(
                                                        text = if (selectedRadioBtn == 0) "₽" else if (selectedRadioBtn == 1) "Лоты" else "",
                                                        style = MaterialTheme.typography.headlineSmall
                                                    )
                                                }
                                            )
                                            TertiaryButton(
                                                text = "Продать",
                                                enabled = instrumentSell.isNotEmpty(),
                                                onClick = {}
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}