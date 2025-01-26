package ru.plumsoftware

import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import ru.plumsoftware.plugins.auth.configBearerAuth
import ru.plumsoftware.plugins.auth.configureAuthRouting
import ru.plumsoftware.plugins.database.configureDatabase
import ru.plumsoftware.plugins.defroute.configureDefaultRouting
import ru.plumsoftware.plugins.serialization.configureSerialization
import ru.plumsoftware.plugins.statistic.configureStatistic
import ru.plumsoftware.plugins.trading.configureTradingRouting

fun main(args: Array<String>) {
    embeddedServer(
        Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    configureDatabase()
    configureDefaultRouting()
    configureSerialization()
    configBearerAuth()

    configureAuthRouting()
    configureTradingRouting()
    configureStatistic()
}
