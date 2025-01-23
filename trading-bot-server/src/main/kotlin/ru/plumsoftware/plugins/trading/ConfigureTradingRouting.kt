package ru.plumsoftware.plugins.trading

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import ru.plumsoftware.net.core.model.receive.trading.TradingModelsReceive
import ru.plumsoftware.plugins.auth.AuthConfig

fun Application.configureTradingRouting() {

    routing {
        authenticate(AuthConfig.BEARER_AUTH) {
            put(path = "trading/models") {
                val tradingModelsReceive = call.receive<TradingModelsReceive>()
            }
        }
    }
}