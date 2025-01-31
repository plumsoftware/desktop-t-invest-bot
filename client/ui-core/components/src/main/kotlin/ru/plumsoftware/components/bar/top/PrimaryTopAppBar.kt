package ru.plumsoftware.components.bar.top

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import ru.plumsoftware.components.button.icon.PrimaryIconWrapper
import ru.plumsoftware.theme.AppTheme

@Composable
fun PrimaryTopAppBar(title: String, onBack: (() -> Unit)? = null) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        title = {
            Text(text = title, style = MaterialTheme.typography.headlineMedium)
        },
        navigationIcon = {
            if (onBack != null)
                PrimaryIconWrapper(
                    enabled = true,
                    icon = Icons.AutoMirrored.Rounded.ArrowBack,
                    onClick = onBack
                )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
private fun PrimaryTopAppBarPreview() {
    AppTheme {
        Scaffold(
            topBar = { PrimaryTopAppBar(title = "Hello world", onBack = {}) }
        ) {

        }
    }
}