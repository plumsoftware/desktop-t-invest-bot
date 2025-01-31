package ru.plumsoftware.model

enum class TradingMode {
    MARKET,
    SANDBOX;

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