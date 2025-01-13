package ru.plumsoftware.core.brokerage.market.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Acc(
    @SerialName("account_id") val accountId: String = ""
)
