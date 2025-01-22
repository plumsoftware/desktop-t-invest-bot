package ru.plumsoftware.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Long,
    val password: String,
    val phone: String,
    val name: String,
    val secretKey: String
) {
    companion object {
        fun empty(): UserDto = UserDto(id = -1L, password = "", phone = "", name = "", secretKey = "")
    }
}
