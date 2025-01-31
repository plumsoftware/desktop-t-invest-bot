package ru.plumsoftware.facade

import ru.plumsoftware.net.core.mapper.toDto
import ru.plumsoftware.net.core.mapper.toReceive
import ru.plumsoftware.net.core.model.receive.StatisticReceive
import ru.plumsoftware.service.statistic.StatisticService

class StatisticFacade(private val statisticService: StatisticService) {

    suspend fun create(statisticReceive: StatisticReceive) {
        statisticService.createStatistic(statisticDto = statisticReceive.toDto())
    }

    suspend fun createToSandbox(statisticReceive: StatisticReceive) {
        statisticService.createToSandboxStatistic(statisticDto = statisticReceive.toDto())
    }

    suspend fun get(id: Long): List<StatisticReceive> {
        return statisticService.getStatistic(id = id).map { it.toReceive() }
    }

    suspend fun getSandboxStatistic(id: Long): List<StatisticReceive> {
        return statisticService.getSandboxStatistic(id = id).map { it.toReceive() }
    }
}