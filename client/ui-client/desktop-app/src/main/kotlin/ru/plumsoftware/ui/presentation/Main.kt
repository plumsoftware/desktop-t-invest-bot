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
import ru.plumsoftware.client.core.auth.AuthRepository
import ru.plumsoftware.client.core.client
import ru.plumsoftware.core.brokerage.market.MarketRepositoryImpl
import ru.plumsoftware.core.brokerage.sandbox.repository.SandboxRepositoryImpl
import ru.plumsoftware.core.settings.repository.SettingsRepositoryImpl
import ru.plumsoftware.log.repository.LogRepositoryImpl
import ru.plumsoftware.navigation.Route
import ru.plumsoftware.navigation.auth.auth.Auth
import ru.plumsoftware.navigation.auth.privacy.policy.PrivacyPolicy
import ru.plumsoftware.navigation.auth.variant.AuthVariant
import ru.plumsoftware.client.core.settings.repository.SettingsRepository
import ru.plumsoftware.theme.AppTheme
import ru.plumsoftware.ui.presentation.screens.select.sandbox.SelectSandboxAccount
import ru.plumsoftware.ui.presentation.screens.main.MainScreen
import ru.plumsoftware.ui.presentation.screens.market.MarketScreen
import ru.plumsoftware.ui.presentation.screens.sandbox.SandboxScreen
import ru.plumsoftware.ui.presentation.screens.select.market.SelectMarketAccount
import ru.plumsoftware.ui.presentation.screens.settings.SettingsScreen
import ru.plumsoftware.ui.root.DesktopRouting

fun main(): Unit = runBlocking {

    val authRepository = AuthRepository(
        client = client(),
        baseUrl = "http://127.0.0.1:8080",
        accessToken = "SECRET", //TODO(remove token)
    )
    val platformSettingsRepository = SettingsRepository()

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
            title = "Торговый робот",
            state = windowState,
        ) {
            PreComposeApp {
                val navigator = rememberNavigator()

                AppTheme(useDarkTheme = isDark) {
                    NavHost(
                        navigator = navigator,
                        navTransition = NavTransition(),
                        initialRoute = Route.Group.AUTH,
                        modifier = Modifier.padding(all = 0.dp)
                    ) {

                        group(route = Route.Group.AUTH, initialRoute = Route.Auth.AUTH_VARIANT) {
                            scene(route = Route.Auth.AUTH_VARIANT) {
                                AuthVariant(
                                    onlyAuth = true,
                                    navigator = navigator
                                )
                            }
                            scene(route = Route.Auth.AUTHENTICATION) {
                                Auth(
                                    navigator = navigator,
                                    needConfirmNumber = false,
                                    authRepository = authRepository,
                                    settingsRepository = platformSettingsRepository
                                )
                            }
                            scene(route = Route.Auth.PRIVACY_POLICY) {
                                PrivacyPolicy(navigator = navigator)
                            }
                        }

                        group(route = Route.Group.MAIN, initialRoute = Route.Main.HOME) {
                            scene(route = Route.Main.HOME) {

                            }
                        }

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