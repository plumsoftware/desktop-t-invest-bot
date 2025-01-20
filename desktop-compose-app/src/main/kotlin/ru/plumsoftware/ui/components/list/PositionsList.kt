package ru.plumsoftware.ui.components.list

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
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
import ru.plumsoftware.core.brokerage.mappers.fromInstrumentTypeToName
import ru.plumsoftware.ui.components.PrimaryTextButton
import ru.plumsoftware.ui.components.TertiaryButton
import ru.plumsoftware.ui.presentation.screens.sandbox.model.Event
import ru.plumsoftware.ui.presentation.screens.sandbox.model.Model
import ru.plumsoftware.ui.theme.Space
import ru.plumsoftware.ui.theme.disabled

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PositionsList(model: State<Model>, onEvent: (Event) -> Unit) {
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
                        onEvent(Event.SelectInstrument(figi = item.figi))
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
                                onClick = {
                                    if (selectedRadioBtn == 0) {
                                        onEvent(Event.BuyWithMoney(money = instrumentBuy))
                                    } else if (selectedRadioBtn == 1) {
                                        onEvent(Event.BuyLot(lot = instrumentBuy))
                                    }
                                }
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
                                onClick = {
                                    if (selectedRadioBtn == 0) {
                                        onEvent(Event.SellWithMoney(money = instrumentSell))
                                    } else if (selectedRadioBtn == 1) {
                                        onEvent(Event.SellLot(lot = instrumentSell))
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}