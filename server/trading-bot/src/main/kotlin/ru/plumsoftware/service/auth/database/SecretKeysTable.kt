package ru.plumsoftware.service.auth.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

object SecretKeysTable : Table("secret_keys") {
    private val id = SecretKeysTable.long("id")
    private val secretKey = SecretKeysTable.text("secret_key")

    suspend fun insert(secretKey: String, id: Long) {
        dbQuery {
            SecretKeysTable.insert {
                it[this.id] = id
                it[this.secretKey] = secretKey
            }
        }
    }


    suspend fun selectById(id: Long): String? {
        return dbQueryReturn {
            val secretKeyResult = SecretKeysTable.select { SecretKeysTable.id.eq(id) }.singleOrNull()
            if (secretKeyResult != null)
                secretKeyResult[this.secretKey]
            else null
        }
    }
}