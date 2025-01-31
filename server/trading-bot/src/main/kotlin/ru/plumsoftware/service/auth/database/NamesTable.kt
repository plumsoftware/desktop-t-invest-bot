package ru.plumsoftware.service.auth.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

object NamesTable : Table("names") {
    private val id = NamesTable.long("id")
    private val name = NamesTable.text("name")

    suspend fun insert(name: String, id: Long) {
        dbQuery {
            NamesTable.insert {
                it[this.id] = id
                it[this.name] = name
            }
        }
    }


    suspend fun selectById(id: Long): String? {
        return dbQueryReturn {
            val nameResult = NamesTable.select { NamesTable.id.eq(id) }.singleOrNull()
            if (nameResult != null)
                nameResult[this.name]
            else null
        }
    }
}