package ru.plumsoftware.core.brokerage.sandbox.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Acc(
    @SerialName("account_id") val accountId: String = ""
)
