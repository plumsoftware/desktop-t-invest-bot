package ru.plumsoftware.ui.presentation.dialogs.select_sandbox_account.model

import ru.tinkoff.piapi.contract.v1.Account
import ru.tinkoff.piapi.core.InvestApi

data class Model(
    val investApi: InvestApi? = null,
    val accounts: List<Account> = emptyList(),
    val selected: Account? = null,
)
