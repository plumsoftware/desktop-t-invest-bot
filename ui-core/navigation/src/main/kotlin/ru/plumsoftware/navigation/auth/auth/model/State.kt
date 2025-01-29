package ru.plumsoftware.navigation.auth.auth.model

data class State(
    val phone: String
) {
    companion object {
        fun default() = State(
            phone = ""
        )
    }
}
