package ru.plumsoftware.ui.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition
import ru.plumsoftware.core.settings.repository.SettingsRepositoryImpl
import ru.plumsoftware.ui.presentation.screens.main.MainScreen
import ru.plumsoftware.ui.presentation.screens.settings.SettingsScreen
import ru.plumsoftware.ui.root.DesktopRouting
import ru.plumsoftware.ui.theme.AppTheme

fun main(): Unit = runBlocking {

    val settingsRepository = SettingsRepositoryImpl()
    val saveIOContext = Dispatchers.IO + SupervisorJob()
    val scope = CoroutineScope(saveIOContext)

    val isDark = scope.async {
        return@async settingsRepository.getSettings().isDark
    }.await()


    application {

        val windowState = rememberWindowState(placement = WindowPlacement.Floating)

        Window(
            onCloseRequest = ::exitApplication,
            title = "T инвест бот",
            state = windowState,
        ) {
            PreComposeApp {
                val navigator = rememberNavigator()


                AppTheme(useDarkTheme = isDark) {
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
                        scene(route = DesktopRouting.settings) {
                            SettingsScreen(
                                settingsRepository = settingsRepository,
                                navigator = navigator
                            )
                        }
                    }
                }
            }
        }
    }
}