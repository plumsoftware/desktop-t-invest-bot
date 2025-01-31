package ru.plumsoftware.net.core.model.receive

import kotlinx.serialization.Serializable

@Serializable
data class PasswordMatchReceive(
    val id: Long,
    val password: String
)
