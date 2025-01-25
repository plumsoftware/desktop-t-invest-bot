package ru.plumsoftware.net.core.model.response

import kotlinx.serialization.Serializable

sealed class UserResponseEither {
    @Serializable
    data class UserResponse(val id: Long) : UserResponseEither() {
        companion object {
            fun empty(): UserResponse = UserResponse(id = -1L)
        }
    }

    @Serializable
    data class Error(val msg: String) : UserResponseEither() {
        companion object {
            fun empty(): Error = Error(msg = "")
        }
    }
}
