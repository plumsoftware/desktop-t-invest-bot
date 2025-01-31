package ru.plumsoftware.navigation.auth.auth.model

import androidx.compose.material3.SnackbarHostState

data class State(
    val phone: String,
    val password: String,
    val snackbarHostState: SnackbarHostState,
    val isLoading: Boolean
) {
    companion object {
        fun default() = State(
            phone = "",
            password = "",
            snackbarHostState = SnackbarHostState(),
            isLoading = false
        )
    }
}
