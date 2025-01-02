package ru.plumsoftware.ui.components.sandbox_tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ru.plumsoftware.ui.components.list.PositionsList
import ru.plumsoftware.ui.presentation.screens.sandbox.model.Event
import ru.plumsoftware.ui.presentation.screens.sandbox.model.Model
import ru.plumsoftware.ui.theme.Space

@Composable
fun InstrumentsTab(model: State<Model>, onEvent: (Event) -> Unit) {
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
                            onEvent(Event.SearchInstrument(instrumentId))
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

        PositionsList(model = model, onEvent = onEvent)
    }
}