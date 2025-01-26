package ru.plumsoftware.plugins.trading

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import ru.plumsoftware.facade.AuthFacade
import ru.plumsoftware.facade.StatisticFacade
import ru.plumsoftware.facade.TTradingFacade
import ru.plumsoftware.model.TradingMode
import ru.plumsoftware.net.core.model.receive.StatisticReceive
import ru.plumsoftware.net.core.model.receive.trading.TradingModelsReceive
import ru.plumsoftware.plugins.auth.AuthConfig
import ru.plumsoftware.repository.market.MarketRepositoryImpl
import ru.plumsoftware.repository.sandbox.SandboxRepositoryImpl
import ru.plumsoftware.service.auth.AuthService
import ru.plumsoftware.service.statistic.StatisticService
import service.cryptography.CryptographyService
import service.hash.HashService
import java.util.Calendar

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

    val statisticFacade = StatisticFacade(
        statisticService = StatisticService()
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
                    else {
                        tTradingFacade.init(tTokensReceive = tTokensReceive)
                        call.respond(HttpStatusCode.OK)
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            get(path = "trading/t/sandbox/accounts") {
                try {
                    val list = tTradingFacade.getAccounts()
                    call.respond(HttpStatusCode.OK, list)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "")
                }
            }
            get(path = "trading/t/sandbox/init_account/{name}") {
                val name = call.parameters["name"] ?: throw IllegalArgumentException("Invalid name")
                try {
                    val accountId = tTradingFacade.initSandboxAccount(name = name)
                    call.respond(HttpStatusCode.OK, accountId)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "")
                }
            }
            post(path = "trading/t/sandbox/close_account") {
                try {
                    tTradingFacade.closeSandboxAccount()
                    call.respond(HttpStatusCode.OK)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "")
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
            get(path = "trading/t/portfolio") {
                try {
                    val modeParam = call.request.queryParameters["mode"]
                        ?: throw IllegalArgumentException("Invalid mode")
                    val mode = TradingMode.fromString(modeParam)
                    if (mode != null) {
                        when (mode) {
                            TradingMode.MARKET -> call.respond(
                                HttpStatusCode.OK,
                                tTradingFacade.getPortfolio()
                            )

                            TradingMode.SANDBOX -> call.respond(
                                HttpStatusCode.OK,
                                tTradingFacade.getSandboxPortfolio()
                            )
                        }
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "")
                }
            }

            post(path = "trading/t/run") {
                try {
                    val modeParam = call.request.queryParameters["mode"]
                        ?: throw IllegalArgumentException("Invalid mode")
                    val idParam = call.request.queryParameters["id"]
                        ?: throw IllegalArgumentException("Invalid id")
                    val mode = TradingMode.fromString(modeParam)

                    if (mode != null) {
                        when (mode) {
                            TradingMode.MARKET -> {
                                val tradingModelsReceive: TradingModelsReceive =
                                    authFacade.getTradingModels(id = idParam.toLong())
                                tTradingFacade.runMarketLimitOrderTrading(tradingModelsReceive = tradingModelsReceive)
                                call.respond(HttpStatusCode.OK)
                            }

                            TradingMode.SANDBOX -> {
                                val tradingModelsReceive: TradingModelsReceive =
                                    authFacade.getSandboxTradingModels(id = idParam.toLong())
                                tTradingFacade.runSandboxTrading(tradingModelsReceive = tradingModelsReceive)
                                call.respond(HttpStatusCode.OK)
                            }
                        }
                    } else {
                        call.respond(HttpStatusCode.BadRequest)
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "")
                }
            }
            get(path = "trading/t/status") {
                try {
                    val id = call.request.queryParameters["id"]
                        ?: throw IllegalArgumentException("Invalid id")
                    val mode = TradingMode.fromString(
                        call.request.queryParameters["mode"]
                            ?: throw IllegalArgumentException("Invalid mode")
                    )

                    if (mode != null) {
                        val status = tTradingFacade.getTradingStatus(id = id.toLong(), mode = mode)
                        call.respond(HttpStatusCode.OK, status)
                    } else {
                        call.respond(HttpStatusCode.BadRequest)
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            post(path = "trading/t/stop") {
                try {
                    val id = call.request.queryParameters["id"]
                        ?: throw IllegalArgumentException("Invalid id")
                    val mode = TradingMode.fromString(
                        call.request.queryParameters["mode"]
                            ?: throw IllegalArgumentException("Invalid mode")
                    )
                    val startValue = call.request.queryParameters["startValue"]
                        ?: throw IllegalArgumentException("Invalid start value")

                    val calendar = Calendar.getInstance()

                    if (mode != null) {
                        when (mode) {
                            TradingMode.MARKET -> {
                                statisticFacade.create(
                                    StatisticReceive(
                                        userId = id.toLong(),
                                        minute = calendar.get(Calendar.MINUTE),
                                        hour = calendar.get(Calendar.HOUR),
                                        day = calendar.get(Calendar.DAY_OF_MONTH),
                                        month = calendar.get(Calendar.MONTH),
                                        year = calendar.get(Calendar.YEAR),
                                        startTradingValue = startValue,
                                        stopTradingValue = tTradingFacade.getPortfolio().totalPortfolio.value
                                    )
                                )
                                tTradingFacade.stopTrading(id = id.toLong())
                                call.respond(HttpStatusCode.OK)
                            }

                            TradingMode.SANDBOX -> {
                                statisticFacade.createToSandbox(
                                    StatisticReceive(
                                        userId = id.toLong(),
                                        minute = calendar.get(Calendar.MINUTE),
                                        hour = calendar.get(Calendar.HOUR),
                                        day = calendar.get(Calendar.DAY_OF_MONTH),
                                        month = calendar.get(Calendar.MONTH),
                                        year = calendar.get(Calendar.YEAR),
                                        startTradingValue = startValue,
                                        stopTradingValue = tTradingFacade.getPortfolio().totalPortfolio.value
                                    )
                                )
                                tTradingFacade.stopSandboxTrading(id = id.toLong())
                                call.respond(HttpStatusCode.OK)
                            }
                        }
                    } else {
                        call.respond(HttpStatusCode.BadRequest)
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            post(path = "destrop/t/api") {
                tTradingFacade.destroy()
                call.respond(HttpStatusCode.OK)
            }

            get(path = "trading/statistic") {
                try {
                    val modeParam = call.request.queryParameters["mode"]
                        ?: throw IllegalArgumentException("Invalid mode")
                    val userId = call.request.queryParameters["id"]
                        ?: throw IllegalArgumentException("Invalid id")
                    val mode = TradingMode.fromString(modeParam)

                    if (mode != null) {
                        val list = when (mode) {
                            TradingMode.MARKET -> statisticFacade.get(id = userId.toLong())
                            TradingMode.SANDBOX -> statisticFacade.getSandboxStatistic(id = userId.toLong())
                        }
                        call.respond(HttpStatusCode.OK, list)
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "")
                }
            }
        }
    }
}