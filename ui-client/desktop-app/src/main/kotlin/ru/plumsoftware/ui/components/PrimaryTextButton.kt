package ru.plumsoftware.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import ru.plumsoftware.ui.theme.Space

@Composable
fun PrimaryTextButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(),
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(horizontal = Space.medium, vertical = Space.small)
    ) {
        Text(text = text, style = MaterialTheme.typography.headlineSmall)
    }
}