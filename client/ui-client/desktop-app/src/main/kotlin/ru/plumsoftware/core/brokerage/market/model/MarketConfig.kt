package ru.plumsoftware.core.brokerage.market.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MarketConfig(
    @SerialName("account") val acc: Acc
)
