package ru.plumsoftware.net.core.model.response.trading.sandbox

import kotlinx.serialization.Serializable

@Serializable
data class SandboxAccountId(
    val name: String,
    val accountId: String
) {
    companion object {
        fun empty() = SandboxAccountId(name = "", accountId = "")
    }
}
