package ru.plumsoftware.plugins.statistic

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import ru.plumsoftware.facade.StatisticFacade
import ru.plumsoftware.net.core.model.receive.StatisticReceive
import ru.plumsoftware.net.core.routing.StatisticRouting
import ru.plumsoftware.plugins.auth.AuthConfig
import ru.plumsoftware.service.statistic.StatisticService
import javax.management.InvalidApplicationException

fun Application.configureStatistic() {

    val statisticFacade = StatisticFacade(
        statisticService = StatisticService()
    )

    routing {
        authenticate(AuthConfig.BEARER_AUTH) {
            get(path = StatisticRouting.GET_STATISTIC) {
                try {
                    val id = call.request.queryParameters[StatisticRouting.Params.ID]
                        ?: throw InvalidApplicationException("Invalid id")

                    val list: List<StatisticReceive> = statisticFacade.get(id = id.toLong())

                    call.respond(HttpStatusCode.OK, list)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "")
                }
            }
        }
    }
}