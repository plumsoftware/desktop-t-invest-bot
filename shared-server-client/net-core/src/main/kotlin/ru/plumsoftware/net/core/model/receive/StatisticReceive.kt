package ru.plumsoftware.net.core.model.receive

import kotlinx.serialization.Serializable

@Serializable
data class StatisticReceive(
    val userId: Long,
    val minute: Int,
    val hour: Int,
    val day: Int,
    val month: Int,
    val year: Int,
    val startTradingValue: String,
    val stopTradingValue: String,
) {
    companion object {
        fun empty() = StatisticReceive(
            userId = -1L,
            minute = -1,
            day = -1,
            month = -1,
            hour = -1,
            year = -1,
            startTradingValue = "",
            stopTradingValue = ""
        )
    }
}
