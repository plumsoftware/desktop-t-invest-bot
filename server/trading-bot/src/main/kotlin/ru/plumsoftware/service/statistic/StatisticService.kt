package ru.plumsoftware.service.statistic

import ru.plumsoftware.net.core.model.dto.StatisticDto
import ru.plumsoftware.service.statistic.database.StatisticSandboxTable
import ru.plumsoftware.service.statistic.database.StatisticTable

class StatisticService {
    suspend fun createStatistic(statisticDto: StatisticDto) {
        StatisticTable.insert(statisticDto = statisticDto)
    }

    suspend fun createToSandboxStatistic(statisticDto: StatisticDto) {
        StatisticSandboxTable.insert(statisticDto = statisticDto)
    }

    suspend fun getStatistic(id: Long) : List<StatisticDto> {
        return StatisticTable.get(id = id)
    }

    suspend fun getSandboxStatistic(id: Long) : List<StatisticDto> {
        return StatisticSandboxTable.get(id = id)
    }
}