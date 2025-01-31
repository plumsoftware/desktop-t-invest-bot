package ru.plumsoftware.components.button.text

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import ru.plumsoftware.theme.AppTheme
import ru.plumsoftware.theme.Space

@Composable
fun TertiaryTextButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    TextButton(
        modifier = Modifier.wrapContentSize(),
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.textButtonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primaryContainer
        ),
        onClick = onClick,
        enabled = enabled
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall.copy(
                textDecoration = TextDecoration.Underline
            )
        )
    }
}

@Composable
@Preview
private fun TertiaryTextButtonPreview() {
    Column(
        modifier = Modifier.padding(all = Space.large),
        verticalArrangement = Arrangement.spacedBy(space = Space.medium)
    ) {
        AppTheme {
            TertiaryTextButton(text = "Hello world", enabled = false, onClick = {})
        }
        AppTheme(useDarkTheme = true) {
            TertiaryTextButton(text = "Hello world", enabled = false, onClick = {})
        }
        AppTheme {
            TertiaryTextButton(text = "Hello world", enabled = true, onClick = {})
        }
        AppTheme(useDarkTheme = true) {
            TertiaryTextButton(text = "Hello world", enabled = true, onClick = {})
        }
    }
}