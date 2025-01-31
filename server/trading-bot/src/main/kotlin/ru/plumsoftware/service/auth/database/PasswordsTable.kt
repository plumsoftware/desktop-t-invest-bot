package ru.plumsoftware.service.auth.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

object PasswordsTable : Table("passwords"){
    private val id = PasswordsTable.long("id")
    private val password = PasswordsTable.text("password")

    suspend fun insert(password: String, id: Long) {
        dbQuery {
            PasswordsTable.insert {
                it[this.id] = id
                it[this.password] = password
            }
        }
    }


    suspend fun selectById(id: Long): String? {
        return dbQueryReturn {
            val passwordResult = PasswordsTable.select { PasswordsTable.id.eq(id) }.singleOrNull()
            if (passwordResult != null)
                passwordResult[this.password]
            else null
        }
    }
}