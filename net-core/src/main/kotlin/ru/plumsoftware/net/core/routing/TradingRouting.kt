package ru.plumsoftware.net.core.routing

object TradingRouting {
    const val POST_T_TRADING_MODELS = "trading/models/t"
    const val GET_TRADING_MODELS = "trading/models/{id}"
    const val POST_INIT_T_TRADING = "trading/t/init/{id}"

    object Params {
        const val MODE = "mode"
        const val ID = "id"
    }
}