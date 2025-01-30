package ru.plumsoftware.ui.components.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.plumsoftware.ui.components.PortfolioComposable
import ru.plumsoftware.ui.components.list.PositionsList2
import ru.plumsoftware.ui.presentation.screens.sandbox.model.Event
import ru.plumsoftware.ui.presentation.screens.sandbox.model.Model
import ru.plumsoftware.ui.theme.Space
import ru.tinkoff.piapi.contract.v1.Instrument

@Composable
fun PortfolioTab(model: State<Model>, onEvent: (Event) -> Unit, getInstrument: (String) -> Instrument?) {
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
        PortfolioComposable(model = model, onEvent = onEvent)
    }

    PositionsList2(model = model, getInstrument = getInstrument)
}