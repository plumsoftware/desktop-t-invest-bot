package ru.plumsoftware.service.auth.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun <T> dbQueryReturn(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO + SupervisorJob()) { block() }

suspend fun dbQuery(block: suspend () -> Unit) =
    newSuspendedTransaction(Dispatchers.IO + SupervisorJob()) { block() }