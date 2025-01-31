package ru.plumsoftware.ui.presentation.screens.select.sandbox.model

import ru.tinkoff.piapi.contract.v1.Account
import ru.tinkoff.piapi.core.InvestApi

data class Model(
    val investApi: InvestApi? = null,
    val accounts: List<Account> = emptyList(),
    val selected: Account? = null,
)
