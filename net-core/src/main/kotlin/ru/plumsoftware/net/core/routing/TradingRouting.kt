package ru.plumsoftware.net.core.routing

object TradingRouting {
    const val POST_T_TRADING_MODELS = "trading/models/t"
    const val GET_TRADING_MODELS = "trading/models/{id}"
    const val POST_INIT_T_TRADING = "trading/t/init/{id}"
    const val GET_TRADING_T_SANDBOX_ACCOUNTS = "trading/t/sandbox/accounts"
    const val GET_TRADING_T_SANDBOX_INIT_ACCOUNT = "trading/t/sandbox/init_account/{name}"
    const val POST_TRADING_T_CLOSE_SANDBOX_ACCOUNT = "trading/t/sandbox/close_account"
    const val GET_TRADING_T_MARKET_INSTRUMENT = "trading/t/market/instrument/{id}"
    const val GET_TRADING_T_PORTFOLIO = "trading/t/portfolio"

    object Params {
        const val MODE = "mode"
        const val ID = "id"
        const val NAME = "name"
    }
}