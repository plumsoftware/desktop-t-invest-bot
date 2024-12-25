package ru.plumsoftware.ui.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition
import ru.plumsoftware.ui.presentation.screens.main.MainScreen
import ru.plumsoftware.ui.root.DesktopRouting
import ru.plumsoftware.ui.theme.AppTheme

fun main() = run {
    application {

        val windowState = rememberWindowState(placement = WindowPlacement.Maximized)

        Window(
            onCloseRequest = ::exitApplication,
            title = "T инвест бот",
            state = windowState,
        ) {
            PreComposeApp {
                val navigator = rememberNavigator()

                AppTheme {
                    NavHost(
                        navigator = navigator,
                        navTransition = NavTransition(),
                        initialRoute = DesktopRouting.home,
                        modifier = Modifier.padding(all = 0.dp)
                    ) {
                        scene(route = DesktopRouting.home) {
                            MainScreen(
                                navigator = navigator
                            )
                        }
                    }
                }
            }
        }
    }
}