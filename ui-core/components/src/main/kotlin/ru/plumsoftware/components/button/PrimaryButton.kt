package ru.plumsoftware.components.button

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.plumsoftware.theme.AppTheme
import ru.plumsoftware.theme.Space

@Composable
fun PrimaryButton(text: String, enabled: Boolean, isLoading: Boolean, onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .defaultMinSize(150.dp, 50.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        enabled = enabled,
        onClick = onClick,
    ) {
        if (isLoading)
            CircularProgressIndicator(
                strokeWidth = 4.dp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        else
            Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview
@Composable
private fun PrimaryButtonPreview() {
    Column(
        modifier = Modifier.padding(all = Space.large),
        verticalArrangement = Arrangement.spacedBy(space = Space.medium)
    ) {
        AppTheme {
            PrimaryButton(text = "Hello world", isLoading = false, enabled = true, onClick = {})
        }
        AppTheme(useDarkTheme = true) {
            PrimaryButton(text = "Hello world", isLoading = false, enabled = false, onClick = {})
        }
        AppTheme {
            PrimaryButton(text = "Hello world", isLoading = true, enabled = false, onClick = {})
        }
        AppTheme(useDarkTheme = true) {
            PrimaryButton(text = "Hello world", isLoading = true, enabled = true, onClick = {})
        }
    }
}