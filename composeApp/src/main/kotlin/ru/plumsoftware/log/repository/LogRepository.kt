package ru.plumsoftware.log.repository

import ru.plumsoftware.log.model.LogMode
import ru.plumsoftware.log.model.LogTradingOperation

interface LogRepository {
    suspend fun write(logTradingOperation: LogTradingOperation, logMode: LogMode = LogMode.SANDBOX)
}