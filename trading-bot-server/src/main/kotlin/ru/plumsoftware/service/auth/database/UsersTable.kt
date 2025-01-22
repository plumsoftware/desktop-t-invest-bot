package ru.plumsoftware.service.auth.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import ru.plumsoftware.model.UserDto

object UsersTable : Table("users") {
    private val name = UsersTable.varchar("name", 255)
    private val password = UsersTable.varchar("password", 255)
    private val phone = UsersTable.varchar("phone", 255)

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO + SupervisorJob()) { block() }

    suspend fun insert(userDto: UserDto) {
        newSuspendedTransaction(Dispatchers.IO + SupervisorJob()) {
            UsersTable.insert {
                it[name] = userDto.name
                it[password] = userDto.password
                it[phone] = userDto.phone
            }
        }
    }


    suspend fun selectByPhone(phone: String): UserDto? {
        return dbQuery {
            val userResult = UsersTable.select { UsersTable.phone.eq(phone) }.singleOrNull()
            if (userResult != null)
                UserDto(
                    password = userResult[password],
                    phone = userResult[UsersTable.phone],
                    name = userResult[name],
                )
            else
                null
        }
    }
}