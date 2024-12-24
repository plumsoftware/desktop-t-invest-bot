package ru.plumsoftware

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = run {
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "T инвест бот",
        ) {

        }
    }
}