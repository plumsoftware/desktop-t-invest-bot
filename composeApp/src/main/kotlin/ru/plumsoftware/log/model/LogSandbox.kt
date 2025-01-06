package ru.plumsoftware.log.model

import java.text.SimpleDateFormat
import java.util.Locale

data class LogSandbox(
    val figi: String,
    val date: String = SimpleDateFormat(
        "dd.MM.yyyy hh:mm:ss",
        Locale.getDefault()
    ).format(System.currentTimeMillis()),
    val countLots: String,
    val currentPrice: String,
    val lastPrice: String,
    val percentIncrease: String,
    val percentDecrease: String,
    val currentPercentChange: String,
    val operation: String,
) {
    override fun toString(): String {
        return "figi: $figi\ndate: $date\ncount lots: $countLots\ncurrent price: $currentPrice\nlast price: $lastPrice\npercent increase: $percentIncrease\npercent decrease: $percentDecrease\ncurrent percent change: $currentPercentChange\noperation: $operation"
    }
}
