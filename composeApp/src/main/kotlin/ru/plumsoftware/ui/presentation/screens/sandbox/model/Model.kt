package ru.plumsoftware.ui.presentation.screens.sandbox.model

import androidx.compose.material3.SnackbarHostState
import ru.tinkoff.piapi.core.InvestApi

data class Model(
    val accountId: String = "",
    val sandboxApi: InvestApi? = null,
    val snackbarHostState: SnackbarHostState = SnackbarHostState(),
    val moneyValue: String = ""
)
