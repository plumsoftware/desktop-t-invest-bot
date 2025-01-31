package ru.plumsoftware.net.core.model.mode

enum class TradingMode {
    MARKET,
    MARGIN;

    companion object {
        fun fromString(value: String): TradingMode? {
            return try {
                valueOf(value.uppercase())
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}