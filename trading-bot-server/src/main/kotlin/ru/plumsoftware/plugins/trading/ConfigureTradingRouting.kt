package ru.plumsoftware.plugins.trading

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import ru.plumsoftware.facade.TTradingFacade
import ru.plumsoftware.net.core.model.receive.trading.TradingModelsReceive
import ru.plumsoftware.plugins.auth.AuthConfig
import ru.plumsoftware.repository.market.MarketRepositoryImpl
import ru.plumsoftware.repository.sandbox.SandboxRepositoryImpl

fun Application.configureTradingRouting() {

    /**
        Only for testing use this variables
    **/
    val market = environment.config.property("trading.t.market").getString()
    val sandbox = environment.config.property("trading.t.sandbox").getString()

    val tTradingFacade = TTradingFacade(
        marketRepository = MarketRepositoryImpl(market), //TODO
        sandboxRepository = SandboxRepositoryImpl(sandbox), //TODO
    )


    routing {
        authenticate(AuthConfig.BEARER_AUTH) {
            put(path = "trading/models") {
                val tradingModelsReceive = call.receive<TradingModelsReceive>()
            }
            get(path = "trading/t/market/instrument/{id}") {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Invalid id")

                try {
                    call.respond(HttpStatusCode.OK, tTradingFacade.getMarketInstrumentBy(id = id))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }
    }
}