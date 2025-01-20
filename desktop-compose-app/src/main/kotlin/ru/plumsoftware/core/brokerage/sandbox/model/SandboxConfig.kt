package ru.plumsoftware.core.brokerage.sandbox.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SandboxConfig(
    @SerialName("account") val acc: Acc
)
