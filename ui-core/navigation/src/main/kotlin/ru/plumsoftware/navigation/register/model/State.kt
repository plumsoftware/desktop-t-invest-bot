package ru.plumsoftware.navigation.register.model

data class State(
    val name: String,
    val password: String
) {
    companion object {
        fun default() = State(name = "", password = "")
    }
}