package ru.plumsoftware.net.core.model.mode

enum class StartsWithMode {
    BUY,
    SELL;

    companion object {
        fun fromString(value: String): StartsWithMode? {
            return try {
                valueOf(value.uppercase())
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}