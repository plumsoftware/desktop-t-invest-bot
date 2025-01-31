package ru.plumsoftware.net.core.model.response.trading

import kotlinx.serialization.Serializable

@Serializable
enum class TradingStatus {
    RUN,
    STOP
}