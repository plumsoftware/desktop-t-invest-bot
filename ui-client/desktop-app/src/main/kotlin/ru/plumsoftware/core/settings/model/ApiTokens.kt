package ru.plumsoftware.core.settings.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("api_tokens")
data class ApiTokens(
    @SerialName("sandbox_token") val sandboxToken: String = "",
    @SerialName("token") val token: String = ""
)
