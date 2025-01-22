package ru.plumsoftware.model.receive

import kotlinx.serialization.Serializable

@Serializable
data class TTokensReceive(
    val id: Long,
    val marketToken: String,
    val sandboxToken: String
) {
    companion object {
        fun empty(): TTokensReceive = TTokensReceive(id = -1L, sandboxToken = "", marketToken = "")
    }
}