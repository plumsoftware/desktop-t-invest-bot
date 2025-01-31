package ru.plumsoftware.net.core.model.response.trading.market

import kotlinx.serialization.Serializable

@Serializable
data class InstrumentResponse(
    val figi: String,
    val ticker: String,
    val name: String,
    val instrumentType: String
)
