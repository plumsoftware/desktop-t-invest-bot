package ru.plumsoftware.net.core.mapper

import ru.plumsoftware.net.core.model.dto.StatisticDto
import ru.plumsoftware.net.core.model.receive.StatisticReceive
import java.util.UUID

fun StatisticReceive.toDto(): StatisticDto {
    return StatisticDto(
        id = UUID.randomUUID().leastSignificantBits,
        userId = userId,
        minute = minute,
        hour = hour,
        day = day,
        month = month,
        year = year,
        startTradingValue = startTradingValue,
        stopTradingValue = stopTradingValue
    )
}

fun StatisticDto.toReceive(): StatisticReceive {
    return StatisticReceive(
        userId = userId,
        minute = minute,
        hour = hour,
        day = day,
        month = month,
        year = year,
        startTradingValue = startTradingValue,
        stopTradingValue = stopTradingValue
    )
}