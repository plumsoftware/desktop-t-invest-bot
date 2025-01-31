package ru.plumsoftware.ui.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
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
import org.jetbrains.compose.resources.InternalResourceApi
import ru.plumsoftware.core.brokerage.market.MarketRepositoryImpl
import ru.plumsoftware.core.brokerage.sandbox.repository.SandboxRepositoryImpl
import ru.plumsoftware.core.settings.repository.SettingsRepositoryImpl
import ru.plumsoftware.log.repository.LogRepositoryImpl
import ru.plumsoftware.ui.presentation.screens.select.sandbox.SelectSandboxAccount
import ru.plumsoftware.ui.presentation.screens.main.MainScreen
import ru.plumsoftware.ui.presentation.screens.market.MarketScreen
import ru.plumsoftware.ui.presentation.screens.sandbox.SandboxScreen
import ru.plumsoftware.ui.presentation.screens.select.market.SelectMarketAccount
import ru.plumsoftware.ui.presentation.screens.settings.SettingsScreen
import ru.plumsoftware.ui.root.DesktopRouting
import ru.plumsoftware.ui.theme.AppTheme

@OptIn(InternalResourceApi::class)
fun main(): Unit = runBlocking {

    val settingsRepository = SettingsRepositoryImpl()
    val sandboxRepository = SandboxRepositoryImpl()
    val logRepository = LogRepositoryImpl()
    val marketRepository = MarketRepositoryImpl()

    val saveIOContext = Dispatchers.IO + SupervisorJob()
    val scope = CoroutineScope(saveIOContext)

    val isDark = scope.async {
        return@async settingsRepository.getSettings().isDark
    }.await()


    application {

        val windowState = rememberWindowState(placement = WindowPlacement.Maximized)

        Window(
            onCloseRequest = ::exitApplication,
            icon = BitmapPainter(useResource("logo.png", ::loadImageBitmap)),
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
                        scene(route = DesktopRouting.sandbox) {
                            SandboxScreen(
                                navigator = navigator,
                                settingsRepository = settingsRepository,
                                sandboxRepository = sandboxRepository,
                                logRepository = logRepository
                            )
                        }
                        scene(route = DesktopRouting.selectSandboxAccountId) {
                            SelectSandboxAccount(
                                navigator = navigator,
                                sandboxRepository = sandboxRepository,
                                settingsRepository = settingsRepository,
                            )
                        }
                        scene(route = DesktopRouting.selectMarketAccountId) {
                            SelectMarketAccount(
                                navigator = navigator,
                                marketRepository = marketRepository,
                                settingsRepository = settingsRepository,
                            )
                        }
                        scene(route = DesktopRouting.market) {
                            MarketScreen(
                                navigator = navigator,
                                settingsRepository = settingsRepository,
                                marketRepository = marketRepository,
                                logRepository = logRepository
                            )
                        }
                    }
                }
            }
        }
    }
}