package ru.plumsoftware.client.core.statistic

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.parameter
import io.ktor.client.request.put
import io.ktor.client.statement.HttpResponse
import io.ktor.http.appendPathSegments
import ru.plumsoftware.net.core.model.receive.StatisticReceive
import ru.plumsoftware.net.core.routing.StatisticRouting

class StatisticRepository(private val client: HttpClient, private val baseUrl: String) {
    suspend fun getStatistic(id: Long): List<StatisticReceive> {
        val response: HttpResponse = client.put(urlString = baseUrl) {
            url {
                appendPathSegments(StatisticRouting.GET_STATISTIC)
                parameter(StatisticRouting.Params.ID, id)
            }
        }

        return if (response.status.value in 200..299) {
            response.body<List<StatisticReceive>>()
        } else throw Exception(response.status.description)
    }
}