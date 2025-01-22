package ru.plumsoftware.model

import kotlinx.serialization.Serializable

@Serializable
data class TTokens(
    val sandbox: String,
    val market: String
) {
    companion object {
        fun empty() : TTokens = TTokens(sandbox = "", market = "")
    }
}
