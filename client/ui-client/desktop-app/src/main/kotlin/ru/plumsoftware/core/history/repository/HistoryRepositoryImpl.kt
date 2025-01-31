package ru.plumsoftware.core.history.repository

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import ru.plumsoftware.core.history.model.History
import ru.plumsoftware.core.history.model.HistoryItem
import ru.plumsoftware.core.history.model.HistoryMode
import ru.plumsoftware.core.history.model.Path
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryRepositoryImpl : HistoryRepository {

    init {
        val sandboxFolder = File(Path.mainPathToHistorySandboxFolder)
        val marketFolder = File(Path.mainPathToHistoryMarketFolder)

        if (!sandboxFolder.exists())
            sandboxFolder.mkdirs()
        if (!marketFolder.exists())
            marketFolder.mkdirs()
    }

    override suspend fun write(start: String, finish: String, mode: HistoryMode) {

        val root = when (mode) {
            HistoryMode.SANDBOX -> Path.mainPathToHistorySandboxFolder

            HistoryMode.MARKET -> Path.mainPathToHistoryMarketFolder
        }

        workWithFile(start, finish, root)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun get(from: Long, to: Long, mode: HistoryMode): List<History> {

        val fromName = SimpleDateFormat("dd_MM_yyyy", Locale.getDefault()).format(Date(from)) + ".cbor"
        val toName = SimpleDateFormat("dd_MM_yyyy", Locale.getDefault()).format(Date(to)) + ".cbor"

        val root = when (mode) {
            HistoryMode.SANDBOX -> Path.mainPathToHistorySandboxFolder
            HistoryMode.MARKET -> Path.mainPathToHistoryMarketFolder
        }

        val rootFolder = File(root)

        val files = rootFolder.listFiles()
        val result = mutableListOf<History>()

        if (files != null) {
            if (files.isNotEmpty()) {
                files.forEachIndexed { _, file ->
                    if (file.name == fromName || file.name == toName) {
                        result.add(
                            Cbor { ignoreUnknownKeys = true }.decodeFromByteArray<History>(
                                file.readBytes()
                            )
                        )
                    }
                }
            }
        }

        return result
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun workWithFile(start: String, finish: String, rootPath: String) {
        val currentTime = System.currentTimeMillis()
        val fileName =
            rootPath + "\\" + SimpleDateFormat("dd_MM_yyyy", Locale.getDefault()).format(
                Date(
                    currentTime
                )
            ) + ".cbor"
        val rootFolder = File(rootPath)

        val listFile = rootFolder.listFiles()

        if (listFile != null) {
            if (listFile.isNotEmpty()) {
                listFile.forEachIndexed { _, file ->
                    if (file.absolutePath == fileName) {
                        val read = file.readBytes()
                        val history =
                            Cbor { ignoreUnknownKeys = true }.decodeFromByteArray<History>(read)
                        history.list.add(HistoryItem(start, finish, currentTime))
                        file.writeBytes(
                            Cbor { ignoreUnknownKeys = true }.encodeToByteArray(
                                history
                            )
                        )
                    } else {
                        file.createNewFile()
                        file.writeBytes(
                            Cbor { ignoreUnknownKeys = true }.encodeToByteArray(
                                History(
                                    list = mutableListOf(
                                        HistoryItem(start, finish, currentTime)
                                    )
                                )
                            )
                        )
                    }
                }
            } else {
                val historyFile = File(fileName)
                historyFile.createNewFile()
                historyFile.writeBytes(
                    Cbor { ignoreUnknownKeys = true }.encodeToByteArray(
                        History(
                            list = mutableListOf(
                                HistoryItem(start, finish, currentTime)
                            )
                        )
                    )
                )
            }
        } else {
            rootFolder.mkdirs()
            val historyFile = File(fileName)
            historyFile.createNewFile()
            historyFile.writeBytes(
                Cbor { ignoreUnknownKeys = true }.encodeToByteArray(
                    History(
                        list = mutableListOf(
                            HistoryItem(start, finish, currentTime)
                        )
                    )
                )
            )
        }
    }
}