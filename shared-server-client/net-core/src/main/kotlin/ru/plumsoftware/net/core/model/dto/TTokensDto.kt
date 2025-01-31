package ru.plumsoftware.net.core.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class TTokensDto(
    val sandboxToken: String,
    val marketToken: String
) {
    companion object {
        fun empty() : TTokensDto = TTokensDto(sandboxToken = "", marketToken = "")
    }
}
