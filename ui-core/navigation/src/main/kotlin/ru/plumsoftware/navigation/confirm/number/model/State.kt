package ru.plumsoftware.navigation.confirm.number.model

data class State(
    val code: String
) {
    companion object {
        fun default() = State(code = "")
    }
}
