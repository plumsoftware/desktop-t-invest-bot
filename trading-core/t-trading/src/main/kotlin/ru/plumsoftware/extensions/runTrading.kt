package ru.plumsoftware.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.plumsoftware.net.core.model.dto.trading.TradingModelDto
import ru.plumsoftware.net.core.model.dto.trading.TradingModelsDto

fun runTrading(tradingModelsDto: TradingModelsDto, tradingScope: CoroutineScope) : List<Job> {
    val id = tradingModelsDto.id
    val tradingModels = tradingModelsDto.tradingModelsDto
    val jobs = mutableListOf<Job>()

    tradingModels.forEach { tradingModelDto: TradingModelDto ->
        val tick = tradingModelDto.tick
        val tradingMode = tradingModelDto.tradingMode
        val startsWith = tradingModelDto.startsWith
        val job = tradingScope.launch {
            while (true) {
                delay(tick)

            }
        }
        jobs.add(job)
    }
    return jobs
}