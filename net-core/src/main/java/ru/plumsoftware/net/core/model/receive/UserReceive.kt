package ru.plumsoftware.net.core.model.receive

import kotlinx.serialization.Serializable

@Serializable
data class UserReceive(
    val password: String,
    val phone: String,
    val name: String
) {
    companion object {
        fun empty() = UserReceive("", "", "")
    }
}
