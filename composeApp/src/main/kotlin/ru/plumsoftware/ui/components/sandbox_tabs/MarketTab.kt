package ru.plumsoftware.ui.components.sandbox_tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.plumsoftware.ui.components.PrimaryButton
import ru.plumsoftware.ui.components.list_items.MarketCard
import ru.plumsoftware.ui.presentation.screens.sandbox.model.Event
import ru.plumsoftware.ui.presentation.screens.sandbox.model.Model
import ru.plumsoftware.ui.theme.Space
import ru.tinkoff.piapi.contract.v1.Instrument

@Composable
fun MarketTab(
    model: State<Model>,
    onEvent: (Event) -> Unit,
    getInstrument: (String) -> Instrument?
) {

    var isStartTrading by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .padding(horizontal = Space.medium, vertical = Space.small),
        verticalArrangement = Arrangement.spacedBy(space = Space.medium, alignment = Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        itemsIndexed(model.value.positions) { _, item ->
            MarketCard(
                position = item,
                isStartTrading = isStartTrading,
                getInstrument = getInstrument,
                onEvent = onEvent
            )
        }
        item {
            PrimaryButton(
                text = if (isStartTrading) "Закончить торги" else "Начать торги",
                onClick = {
                    isStartTrading = !isStartTrading
                    onEvent(Event.StartTrading(isStartTrading = isStartTrading))
                }
            )
        }
    }
}