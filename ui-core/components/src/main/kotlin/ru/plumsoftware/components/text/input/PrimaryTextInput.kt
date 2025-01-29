package ru.plumsoftware.components.text.input

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import ru.plumsoftware.components.button.icon.PrimaryIconWrapper
import ru.plumsoftware.theme.AppTheme
import ru.plumsoftware.theme.Space

@Composable
fun PrimaryTextInput(
    startText: String = "",
    hint: String = "",
    enabled: Boolean,
    onValueChange: (String) -> Unit
) {
    var text by remember { mutableStateOf(startText) }

    OutlinedTextField(
        value = text,
        modifier = Modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.bodyLarge,
        shape = MaterialTheme.shapes.small,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        enabled = enabled,
        placeholder = {
            Text(text = hint, style = MaterialTheme.typography.bodyLarge)
        },
        onValueChange = {
            text = it
            onValueChange(text)
        },
        keyboardActions = KeyboardActions(
            onDone = {
                onValueChange(text)
            }
        ),
        trailingIcon = {
            PrimaryIconWrapper(
                enabled = text.isNotEmpty(),
                icon = Icons.Rounded.Clear,
                onClick = {
                    text = ""
                }
            )
        }
    )
}

@Composable
@Preview
private fun PrimaryTextInputPreview() {
    Column(
        modifier = Modifier.padding(all = Space.large),
        verticalArrangement = Arrangement.spacedBy(space = Space.medium)
    ) {
        AppTheme {
            PrimaryTextInput(
                startText = "",
                hint = "Hello world",
                enabled = true,
                onValueChange = {}
            )
        }
        AppTheme(useDarkTheme = true) {
            PrimaryTextInput(
                startText = "",
                hint = "",
                enabled = true,
                onValueChange = {}
            )
        }
    }
}