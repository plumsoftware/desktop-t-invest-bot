package ru.plumsoftware.log.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.plumsoftware.log.model.Log
import ru.plumsoftware.log.model.LogSandbox
import java.io.File

class LogRepositoryImpl : LogRepository {

    init {
        val file = File(Log.Path.Sandbox.mainPathToSandboxLogFolder)

        if (!file.exists()) {
            file.mkdirs()
        }
    }

    override suspend fun write(logSandbox: LogSandbox) {
        val path = Log.Path.Sandbox.mainPathToSandboxLogFolder + "\\${logSandbox.name}-log\\${logSandbox.accountId}-log"
        val folder = File(path)

        if (!folder.exists())
            folder.mkdirs()

        val pathToFile =
            Log.Path.Sandbox.mainPathToSandboxLogFolder + "\\${logSandbox.name}-log\\${logSandbox.accountId}-log\\${logSandbox.name}-${logSandbox.figi}-log.txt"
        val file = File(pathToFile)
        if (!file.exists())
            withContext(Dispatchers.IO) {
                file.createNewFile()
            }

        val text = "$logSandbox\n=============================\n"
        file.appendText(
            text
        )
    }
}