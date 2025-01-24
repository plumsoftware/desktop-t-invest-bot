package ru.plumsoftware.plugins.trading

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import ru.plumsoftware.facade.AuthFacade
import ru.plumsoftware.facade.TTradingFacade
import ru.plumsoftware.model.TradingMode
import ru.plumsoftware.net.core.model.receive.trading.TradingModelsReceive
import ru.plumsoftware.plugins.auth.AuthConfig
import ru.plumsoftware.repository.market.MarketRepositoryImpl
import ru.plumsoftware.repository.sandbox.SandboxRepositoryImpl
import ru.plumsoftware.service.auth.AuthService
import service.cryptography.CryptographyService
import service.hash.HashService

fun Application.configureTradingRouting() {

    val config = environment.config
    val authFacade = AuthFacade(
        authService = AuthService(),
        hashService = HashService(config),
        cryptographyService = CryptographyService(config)
    )

    val tTradingFacade = TTradingFacade(
        marketRepository = MarketRepositoryImpl(),
        sandboxRepository = SandboxRepositoryImpl(),
    )


    routing {
        authenticate(AuthConfig.BEARER_AUTH) {
            post(path = "trading/models/t") {
                val modeParam = call.request.queryParameters["mode"]
                    ?: throw IllegalArgumentException("Invalid id")
                val mode = TradingMode.fromString(modeParam)

                if (mode == null) {
                    call.respond(HttpStatusCode.BadRequest)
                } else {
                    //Add to database
                    try {
                        val tradingModelsReceive = call.receive<TradingModelsReceive>()
                        when (mode) {
                            TradingMode.MARKET -> {
                                authFacade.insertTradingModels(tradingModelsReceive = tradingModelsReceive)
                                call.respond(HttpStatusCode.OK)
                            }

                            TradingMode.SANDBOX -> {
                                authFacade.insertSandboxTradingModels(tradingModelsReceive = tradingModelsReceive)
                                call.respond(HttpStatusCode.OK)
                            }
                        }
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest)
                    }
                }
            }
            post(path = "trading/t/init/{id}") {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Invalid id")
                try {
                    val tTokensReceive = authFacade.getTTokens(id = id.toLong())
                    if (tTokensReceive == null)
                        call.respond(HttpStatusCode.BadRequest)
                    else
                        tTradingFacade.init(tTokensReceive = tTokensReceive)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            get(path = "trading/t/market/instrument/{id}") {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Invalid id")

                try {
                    call.respond(HttpStatusCode.OK, tTradingFacade.getMarketInstrumentBy(id = id))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            put(path = "trading/t/run") {

            }
        }
    }
}