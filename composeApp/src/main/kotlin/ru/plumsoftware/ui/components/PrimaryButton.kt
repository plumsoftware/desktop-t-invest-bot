package ru.plumsoftware.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import ru.plumsoftware.ui.theme.AppTheme
import androidx.compose.desktop.ui.tooling.preview.Preview

@Composable
fun PrimaryButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(),
        enabled = enabled,
        shape = MaterialTheme.shapes.small
    ) {
        Text(text = text, style = MaterialTheme.typography.headlineSmall)
    }
}

@Preview
@Composable
private fun PrimaryButtonPreview() {
    AppTheme {
        PrimaryButton(text = "Hello world", onClick = {})
    }
}