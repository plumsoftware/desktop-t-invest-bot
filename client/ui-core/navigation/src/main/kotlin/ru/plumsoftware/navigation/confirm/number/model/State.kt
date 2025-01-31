package ru.plumsoftware.navigation.confirm.number.model

data class State(
    val code: String,
    val phoneNumber: String
) {
    companion object {
        fun default() = State(code = "", phoneNumber = "")
    }
}
