package ru.plumsoftware.ui.components.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.plumsoftware.core.brokerage.mappers.fromInstrumentTypeStrToRuStr
import ru.plumsoftware.ui.components.PrimaryTextButton
import ru.plumsoftware.ui.presentation.screens.sandbox.model.Model
import ru.plumsoftware.ui.theme.Space
import ru.tinkoff.piapi.contract.v1.Instrument

@Composable
fun PositionsList2(model: State<Model>, getInstrument: (String) -> Instrument?) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(
            space = Space.medium,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.Start
    ) {
        itemsIndexed(model.value.positions) { _, item ->
            Column(
                modifier = Modifier
                    .padding(horizontal = Space.large, vertical = Space.medium),
                verticalArrangement = Arrangement.spacedBy(
                    space = Space.medium,
                    alignment = Alignment.CenterVertically
                ),
                horizontalAlignment = Alignment.Start
            ) {
                PrimaryTextButton(
                    text = "${getInstrument(item.figi)?.name} | ${fromInstrumentTypeStrToRuStr(item.instrumentType)}\nВ портфеле ${item.quantity}",
                    onClick = {}
                )
            }
        }
    }
}