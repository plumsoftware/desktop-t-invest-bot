package ru.plumsoftware.components.button

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ru.plumsoftware.theme.AppTheme
import ru.plumsoftware.theme.Space

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrimaryCheckBox(onCheckedChange: (Boolean) -> Unit) {
    var checked by remember { mutableStateOf(false) }
    Checkbox(
        checked = checked,
        onCheckedChange = {
            checked = !checked
            onCheckedChange(checked)
        }
    )
}

@Composable
@Preview
fun PrimaryCheckBoxPreview() {
    Column(
        modifier = Modifier.padding(all = Space.large),
        verticalArrangement = Arrangement.spacedBy(space = Space.medium)
    ) {
        AppTheme(useDarkTheme = false) {
            PrimaryCheckBox(onCheckedChange = {})
        }

        AppTheme(useDarkTheme = true) {
            PrimaryCheckBox(onCheckedChange = {})
        }
    }
}