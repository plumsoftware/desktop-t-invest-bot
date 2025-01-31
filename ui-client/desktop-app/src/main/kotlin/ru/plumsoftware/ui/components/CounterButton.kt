package ru.plumsoftware.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import ru.plumsoftware.ui.theme.Space

@Composable
fun CounterButton(
    value: String,
    enabled: Boolean = true,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(
            space = Space.small,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            enabled = enabled,
            onClick = onIncrease
        ) {
            Icon(
                modifier = Modifier.rotate(180f),
                imageVector = Icons.Rounded.ArrowDropDown,
                contentDescription = "Увеличить значение величены"
            )
        }

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )

        IconButton(
            enabled = enabled,
            onClick = onDecrease
        ) {
            Icon(
                imageVector = Icons.Rounded.ArrowDropDown,
                contentDescription = "Увеньшить значение величены"
            )
        }
    }
}