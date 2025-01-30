package ru.plumsoftware.components.button.text

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.plumsoftware.theme.AppTheme
import ru.plumsoftware.theme.Space

@Composable
fun PrimaryTextButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    TextButton(
        modifier = Modifier
            .defaultMinSize(150.dp, 50.dp),
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.textButtonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground
        ),
        onClick = onClick,
        enabled = enabled
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
@Preview
private fun PrimaryTextButtonPreview() {
    Column(
        modifier = Modifier.padding(all = Space.large),
        verticalArrangement = Arrangement.spacedBy(space = Space.medium)
    ) {
        AppTheme {
            PrimaryTextButton(text = "Hello world", enabled = false, onClick = {})
        }
        AppTheme(useDarkTheme = true) {
            PrimaryTextButton(text = "Hello world", enabled = false, onClick = {})
        }
        AppTheme {
            PrimaryTextButton(text = "Hello world", enabled = true, onClick = {})
        }
        AppTheme(useDarkTheme = true) {
            PrimaryTextButton(text = "Hello world", enabled = true, onClick = {})
        }
    }
}