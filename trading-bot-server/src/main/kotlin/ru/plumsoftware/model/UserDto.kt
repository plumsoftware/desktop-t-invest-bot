package ru.plumsoftware.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val password: String,
    val phone: String,
    val name: String
) {
    companion object {
        fun empty(): UserDto = UserDto(password = "", phone = "", name = "")
    }
}
