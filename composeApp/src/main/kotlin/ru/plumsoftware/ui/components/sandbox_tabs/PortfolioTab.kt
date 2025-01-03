package ru.plumsoftware.ui.components.sandbox_tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ru.plumsoftware.ui.components.PortfolioComposable
import ru.plumsoftware.ui.components.PrimaryTextButton
import ru.plumsoftware.ui.components.list.PositionsList2
import ru.plumsoftware.ui.presentation.screens.sandbox.model.Event
import ru.plumsoftware.ui.presentation.screens.sandbox.model.Model
import ru.plumsoftware.ui.theme.Space

@Composable
fun PortfolioTab(model: State<Model>, onEvent: (Event) -> Unit, getName: (String) -> String) {
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
                onEvent(Event.ChangeMoneyValue(moneyValue = text))
            },
            trailingIcon = {
                Text(text = "₽", style = MaterialTheme.typography.headlineSmall)
            }
        )

        PrimaryTextButton(
            text = "Пополнить",
            enabled = model.value.moneyValue.isNotEmpty(),
            onClick = {
                onEvent(Event.AddMoney)
            }
        )
    }

    PositionsList2(model = model, onEvent = onEvent, getName = getName)
}