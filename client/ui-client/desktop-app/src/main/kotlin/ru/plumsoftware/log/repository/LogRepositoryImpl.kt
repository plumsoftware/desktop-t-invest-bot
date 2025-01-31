package ru.plumsoftware.log.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.plumsoftware.log.model.Log
import ru.plumsoftware.log.model.LogMode
import ru.plumsoftware.log.model.LogTradingOperation
import java.io.File

class LogRepositoryImpl : LogRepository {

    init {
        val file = File(Log.Path.Sandbox.mainPathToSandboxLogFolder)

        if (!file.exists()) {
            file.mkdirs()
        }
    }

    override suspend fun write(logTradingOperation: LogTradingOperation, logMode: LogMode) {
        val path = when(logMode){
            LogMode.SANDBOX -> Log.Path.Sandbox.mainPathToSandboxLogFolder + "\\${logTradingOperation.name}-log\\${logTradingOperation.accountId}-log"
            LogMode.MARKET -> Log.Path.Market.mainPathToMarketLogFolder + "\\${logTradingOperation.name}-log\\${logTradingOperation.accountId}-log"
        }
        val folder = File(path)

        if (!folder.exists())
            folder.mkdirs()

        val pathToFile = when(logMode) {
            LogMode.SANDBOX -> Log.Path.Sandbox.mainPathToSandboxLogFolder + "\\${logTradingOperation.name}-log\\${logTradingOperation.accountId}-log\\${logTradingOperation.name}-${logTradingOperation.figi}-log.txt"
            LogMode.MARKET -> Log.Path.Market.mainPathToMarketLogFolder + "\\${logTradingOperation.name}-log\\${logTradingOperation.accountId}-log\\${logTradingOperation.name}-${logTradingOperation.figi}-log.txt"
        }

        val file = File(pathToFile)
        if (!file.exists())
            withContext(Dispatchers.IO) {
                file.createNewFile()
            }

        val text = "$logTradingOperation\n=============================\n"
        file.appendText(
            text
        )
    }
}