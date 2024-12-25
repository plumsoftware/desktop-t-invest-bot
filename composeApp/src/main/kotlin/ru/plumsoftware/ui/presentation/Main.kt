package ru.plumsoftware.ui.presentation

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ru.plumsoftware.ui.theme.AppTheme

fun main() = run {
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "T инвест бот",
        ) {
            AppTheme {

            }
        }
    }
}