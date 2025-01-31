package ru.plumsoftware.navigation.register.model

data class State(
    val phone: String,
    val name: String,
    val password: String
) {
    companion object {
        fun default() =
            State(phone = "", name = "", password = "")
    }
}