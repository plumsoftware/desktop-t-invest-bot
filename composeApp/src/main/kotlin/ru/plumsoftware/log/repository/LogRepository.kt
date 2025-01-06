package ru.plumsoftware.log.repository

import ru.plumsoftware.log.model.LogSandbox

interface LogRepository {
    suspend fun write(logSandbox: LogSandbox)
}