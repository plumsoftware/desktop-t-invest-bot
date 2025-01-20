package ru.plumsoftware.core.history.model

import kotlinx.serialization.Serializable

@Serializable
data class HistoryItem(
    val start: String,
    val finish: String,
    val date: Long
)
