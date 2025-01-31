package ru.plumsoftware.service.auth.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

object PhonesTable : Table("phones") {
    private val id = PhonesTable.long("id")
    private val phone = PhonesTable.text("phone")

    suspend fun insert(phone: String, id: Long) {
        dbQuery {
            PhonesTable.insert {
                it[this.id] = id
                it[this.phone] = phone
            }
        }
    }


    suspend fun selectById(id: Long): String? {
        return dbQueryReturn {
            val phoneResult = PhonesTable.select { PhonesTable.id.eq(id) }.singleOrNull()
            if (phoneResult != null)
                phoneResult[this.phone]
            else null
        }
    }

    suspend fun getAllPhones(): Map<Long, String> {
        return dbQueryReturn {
            PhonesTable.selectAll().associate { it[id] to it[phone] }
        }
    }
}