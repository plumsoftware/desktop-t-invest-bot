package ru.plumsoftware.ui.presentation.screens.market.model

import androidx.compose.material3.SnackbarHostState
import ru.plumsoftware.core.brokerage.model.TradingModel
import ru.tinkoff.piapi.core.InvestApi
import ru.tinkoff.piapi.core.models.Money
import ru.tinkoff.piapi.core.models.Portfolio

data class Model(
    val accountId: String = "",
    val api: InvestApi? = null,
    val portfolio: Portfolio? = null,
    val stopTradingTotalAmountPortfolio: Money? = null,

    val selectedFigi: String = "",
    val tradingModels: MutableList<TradingModel> = mutableListOf(),
    val isStartTrading: Boolean = false,

    val snackbarHostState: SnackbarHostState = SnackbarHostState()
)
