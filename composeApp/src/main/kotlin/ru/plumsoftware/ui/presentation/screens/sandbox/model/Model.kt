package ru.plumsoftware.ui.presentation.screens.sandbox.model

import androidx.compose.material3.SnackbarHostState
import ru.tinkoff.piapi.contract.v1.InstrumentShort
import ru.tinkoff.piapi.core.InvestApi
import ru.tinkoff.piapi.core.models.Portfolio
import ru.tinkoff.piapi.core.models.Position

data class Model(
    val accountId: String = "",
    val sandboxApi: InvestApi? = null,
    val snackbarHostState: SnackbarHostState = SnackbarHostState(),
    val moneyValue: String = "",
    val portfolio: Portfolio? = null,

    val instrumentsBy: List<InstrumentShort> = emptyList(),

    val selectedFigi: String = "",
    val positions: MutableList<Position> = mutableListOf()
)
