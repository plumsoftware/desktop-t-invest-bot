package ru.plumsoftware.core.history.repository

import ru.plumsoftware.core.history.model.History
import ru.plumsoftware.core.history.model.HistoryMode

interface HistoryRepository {

    suspend fun write(start: String, finish: String, mode: HistoryMode)
    suspend fun get(from: Long, to: Long, mode: HistoryMode) : List<History>
}