package ru.plumsoftware.components.button

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.plumsoftware.theme.AppTheme
import ru.plumsoftware.theme.Space
import ru.plumsoftware.theme.disabled

@Composable
fun PrimaryOutlinedButton(text: String, enabled: Boolean, isLoading: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        modifier = Modifier
            .defaultMinSize(150.dp, 50.dp),
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primaryContainer
        ),
        border = BorderStroke(
            width = 2.dp,
            color = if (enabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primaryContainer.disabled()
        ),
        enabled = enabled,
        onClick = onClick,
    ) {
        if (isLoading)
            CircularProgressIndicator(
                strokeWidth = 4.dp,
                color = MaterialTheme.colorScheme.primaryContainer
            )
        else
            Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview
@Composable
private fun PrimaryOutlinedButtonPreview() {
    Column(
        modifier = Modifier.padding(all = Space.large),
        verticalArrangement = Arrangement.spacedBy(space = Space.medium)
    ) {
        AppTheme {
            PrimaryOutlinedButton(
                text = "Hello world",
                isLoading = false,
                enabled = true,
                onClick = {})
        }
        AppTheme(useDarkTheme = true) {
            PrimaryOutlinedButton(
                text = "Hello world",
                isLoading = false,
                enabled = false,
                onClick = {})
        }
        AppTheme {
            PrimaryOutlinedButton(
                text = "Hello world",
                isLoading = true,
                enabled = false,
                onClick = {})
        }
        AppTheme(useDarkTheme = true) {
            PrimaryOutlinedButton(
                text = "Hello world",
                isLoading = true,
                enabled = true,
                onClick = {})
        }
    }
}