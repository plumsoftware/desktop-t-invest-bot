package ru.plumsoftware.core.history.model

import kotlinx.serialization.Serializable

@Serializable
data class History(
    val list: MutableList<HistoryItem>
)
