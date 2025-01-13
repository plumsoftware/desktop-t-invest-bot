package ru.plumsoftware.ui.components.list_items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import ru.plumsoftware.core.brokerage.mappers.fromInstrumentTypeStrToRuStr
import ru.plumsoftware.core.brokerage.sandbox.model.Sandbox
import ru.plumsoftware.ui.components.CounterButton
import ru.plumsoftware.ui.theme.Space
import ru.plumsoftware.ui.theme.disabled
import ru.tinkoff.piapi.contract.v1.Instrument
import ru.tinkoff.piapi.core.models.Position

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketCard(
    position: Position,
    isStartTrading: Boolean,
    getInstrument: (String) -> Instrument?,
    onCheckedChange: (String, Int, String, String, Boolean) -> Unit,
) {

    val instrument = getInstrument(position.figi)

    if (instrument != null) {
        val lot = position.quantity.toInt() / instrument.lot - 1
        var value by remember { mutableIntStateOf(1) }
        var checked by remember { mutableStateOf(false) }

        var increase by remember { mutableStateOf(Sandbox.COMMISSION_PERCENT.toString()) }
        var decrease by remember { mutableStateOf(Sandbox.COMMISSION_PERCENT.toString()) }

        var enabledCard by remember { mutableStateOf(!checked && !isStartTrading) }

        LaunchedEffect(isStartTrading, checked) {
            enabledCard = !isStartTrading && !checked
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(
                space = Space.small,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        space = Space.small,
                        alignment = Alignment.Start
                    ),
                    modifier = Modifier.fillMaxWidth().wrapContentHeight()
                ) {
                    Column(
                        modifier = Modifier.weight(1.0f),
                        verticalArrangement = Arrangement.spacedBy(
                            space = Space.medium,
                            alignment = Alignment.Top
                        ),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(
                                space = Space.small,
                                alignment = Alignment.Start
                            ),
                            modifier = Modifier.wrapContentSize()
                        ) {
                            Text(
                                text = instrument.name,
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Divider(
                                thickness = 2.dp,
                                modifier = Modifier
                                    .height(18.dp)
                                    .width(2.dp)
                                    .clip(shape = MaterialTheme.shapes.small),
                                color = MaterialTheme.colorScheme.onBackground.disabled()
                            )
                            Text(
                                text = fromInstrumentTypeStrToRuStr(instrumentType = position.instrumentType),
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                        Text(
                            text = "В портфеле ${
                                String
                                    .format("%.5f", position.quantity.toFloat())
                                    .replace(",", ".")
                            } штук",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = "Цена ${
                                String
                                    .format("%.3f", position.currentPrice.value.toFloat())
                                    .replace(",", ".")
                            } ${position.currentPrice.currency} за одну бумагу",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }

                    Spacer(modifier = Modifier.weight(0.3f))

                    Text(
                        text = "К торгам",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    CounterButton(
                        value = value.toString(),
                        enabled = enabledCard,
                        onIncrease = {
                            if (value < lot)
                                value += 1
                        },
                        onDecrease = {
                            if (value > 1)
                                value -= 1
                        }
                    )

                    Text(
                        text = "лот",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.weight(0.3f))

                    OutlinedTextField(
                        value = increase,
                        enabled = enabledCard,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.KeyboardArrowUp,
                                contentDescription = "Верхняя граница цены (в процентах)",
                                tint = Color.Green
                            )
                        },
                        textStyle = MaterialTheme.typography.headlineSmall,
                        trailingIcon = {
                            Text(
                                text = "%",
                                style = MaterialTheme.typography.headlineSmall.copy(color = MaterialTheme.colorScheme.onBackground.disabled())
                            )
                        },
                        onValueChange = {
                            increase = it
                        }
                    )
                    OutlinedTextField(
                        value = decrease,
                        enabled = enabledCard,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.KeyboardArrowDown,
                                contentDescription = "Нижняя граница цены (в процентах)",
                                tint = Color.Red
                            )
                        },
                        textStyle = MaterialTheme.typography.headlineSmall,
                        trailingIcon = {
                            Text(
                                text = "%",
                                style = MaterialTheme.typography.headlineSmall.copy(color = MaterialTheme.colorScheme.onBackground.disabled())
                            )
                        },
                        onValueChange = {
                            decrease = it
                        }
                    )

                    Checkbox(
                        checked = checked,
                        enabled = !isStartTrading,
                        onCheckedChange = {
                            checked = it
                            enabledCard = !checked
                            onCheckedChange.invoke(
                                instrument.figi,
                                value,
                                increase,
                                decrease,
                                checked
                            )
                        }
                    )
                }
            }
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Space.large)
                    .clip(shape = MaterialTheme.shapes.small),
                color = MaterialTheme.colorScheme.onBackground.disabled(),
                thickness = 4.dp
            )
        }
    }
}