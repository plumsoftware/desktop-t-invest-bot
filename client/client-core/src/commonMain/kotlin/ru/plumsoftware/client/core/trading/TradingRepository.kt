package ru.plumsoftware.client.core.trading

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.appendPathSegments
import io.ktor.http.parameters
import ru.plumsoftware.net.core.model.receive.StatisticReceive
import ru.plumsoftware.net.core.model.receive.trading.TradingModelsReceive
import ru.plumsoftware.net.core.model.response.trading.TradingStatus
import ru.plumsoftware.net.core.model.response.trading.market.InstrumentResponse
import ru.plumsoftware.net.core.model.response.trading.market.PortfolioResponse
import ru.plumsoftware.net.core.model.response.trading.sandbox.SandboxAccountId
import ru.plumsoftware.net.core.routing.TradingRouting

class TradingRepository(private val client: HttpClient, private val baseUrl: String) {
    suspend fun postTTradingModels(
        tradingModelsReceive: TradingModelsReceive,
        mode: String
    ): HttpStatusCode {
        val response: HttpResponse = client.post(urlString = baseUrl) {
            url {
                appendPathSegments(TradingRouting.POST_T_TRADING_MODELS)
                parameter(TradingRouting.Params.MODE, mode)
            }
            setBody(tradingModelsReceive)
        }
        return response.status
    }

    suspend fun getTTradingModels(id: Long): TradingModelsReceive {
        val response: HttpResponse = client.get(urlString = baseUrl) {
            url {
                appendPathSegments(TradingRouting.GET_TRADING_MODELS.replace("{id}", id.toString()))
            }
        }

        return if (response.status.value in 200..299) {
            response.body<TradingModelsReceive>()
        } else TradingModelsReceive.empty()
    }

    suspend fun postInitTTrading(id: Long): HttpStatusCode {
        val response: HttpResponse = client.post(urlString = baseUrl) {
            url {
                appendPathSegments(
                    TradingRouting.POST_INIT_T_TRADING.replace(
                        "{id}",
                        id.toString()
                    )
                )
            }
        }

        return response.status
    }

    suspend fun getTSandboxAccounts(): List<SandboxAccountId> {
        val response: HttpResponse = client.get(urlString = baseUrl) {
            url {
                appendPathSegments(TradingRouting.GET_TRADING_T_SANDBOX_ACCOUNTS)
            }
        }

        return if (response.status.value in 200..299) {
            response.body<List<SandboxAccountId>>()
        } else emptyList()
    }

    suspend fun getInitTSandboxAccount(name: String): SandboxAccountId {
        val response: HttpResponse = client.get(urlString = baseUrl) {
            url {
                appendPathSegments(
                    TradingRouting.GET_TRADING_T_SANDBOX_INIT_ACCOUNT.replace(
                        "{name}",
                        name
                    )
                )
            }
        }

        return if (response.status.value in 200..299) {
            response.body<SandboxAccountId>()
        } else SandboxAccountId.empty()
    }

    suspend fun postCloseTTradingSandboxAccount(): HttpStatusCode {
        val response: HttpResponse = client.post(urlString = baseUrl) {
            url {
                appendPathSegments(TradingRouting.POST_TRADING_T_CLOSE_SANDBOX_ACCOUNT)
            }
        }

        return response.status
    }

    suspend fun getTTradingMarketInstrument(id: String): List<InstrumentResponse> {
        val response = client.get(urlString = baseUrl) {
            url {
                appendPathSegments(
                    TradingRouting.GET_TRADING_T_MARKET_INSTRUMENT.replace(
                        "{id}",
                        id
                    )
                )
            }
        }

        return if (response.status.value in 200..299)
            response.body<List<InstrumentResponse>>()
        else
            emptyList()
    }

    suspend fun getTPortfolio(mode: String): PortfolioResponse {
        val response = client.get(urlString = baseUrl) {
            url {
                appendPathSegments(TradingRouting.GET_TRADING_T_PORTFOLIO)
                parameter(TradingRouting.Params.MODE, mode)
            }
        }
        return if (response.status.value in 200..299)
            response.body<PortfolioResponse>()
        else PortfolioResponse.empty()
    }

    suspend fun postRunTTrading(mode: String, id: Long): HttpStatusCode {
        val response = client.post(urlString = baseUrl) {
            url {
                appendPathSegments(TradingRouting.POST_TRADING_T_RUN)
                parameters {
                    parameter(TradingRouting.Params.MODE, mode)
                    parameter(TradingRouting.Params.ID, id)
                }
            }
        }
        return response.status
    }

    suspend fun getTTradingStatus(mode: String, id: Long): TradingStatus? {
        val response = client.post(urlString = baseUrl) {
            url {
                appendPathSegments(TradingRouting.GET_TRADING_T_STATUS)
                parameters {
                    parameter(TradingRouting.Params.MODE, mode)
                    parameter(TradingRouting.Params.ID, id)
                }
            }
        }

        return if (response.status.value in 200..299)
            response.body<TradingStatus>()
        else null
    }

    suspend fun postStopTTrading(mode: String, id: Long, startValue: String): HttpStatusCode {
        val response = client.post(urlString = baseUrl) {
            url {
                appendPathSegments(TradingRouting.POST_TRADING_T_STOP)
                parameters {
                    parameter(TradingRouting.Params.MODE, mode)
                    parameter(TradingRouting.Params.ID, id)
                    parameter(TradingRouting.Params.START_VALUE, startValue)
                }
            }
        }

        return response.status
    }

    suspend fun postDestroyTApi(): HttpStatusCode {
        val response = client.post(urlString = baseUrl) {
            url {
                appendPathSegments(TradingRouting.POST_DESTROY_T_API)
            }
        }
        return response.status
    }

    suspend fun getTTradingStatistic(id: Long, mode: String): List<StatisticReceive> {
        val response = client.get(urlString = baseUrl) {
            url {
                appendPathSegments(TradingRouting.GET_TRADING_T_STATISTIC)
                parameters {
                    parameter(TradingRouting.Params.MODE, mode)
                    parameter(TradingRouting.Params.ID, id)
                }
            }
        }

        return if (response.status.value in 200..299)
            response.body<List<StatisticReceive>>()
        else emptyList()
    }
}