package ru.plumsoftware.service.auth.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import ru.plumsoftware.model.dto.TTokensDto

object TTokensTable : Table("t_tokens") {
    private val id = TTokensTable.long("id")
    private val sandboxToken = TTokensTable.text("sandbox_token")
    private val marketToken = TTokensTable.text("market_token")

    suspend fun insert(marketToken: String, sandboxToken: String, id: Long) {
        dbQuery {
            TTokensTable.insert {
                it[this.id] = id
                it[this.sandboxToken] = sandboxToken
                it[this.marketToken] = marketToken
            }
        }
    }

    suspend fun selectById(id: Long): TTokensDto? {
        return dbQueryReturn {
            val tokensResult = TTokensTable.select { TTokensTable.id.eq(id) }.singleOrNull()
            if (tokensResult != null)
                TTokensDto(
                    sandboxToken = tokensResult[this.sandboxToken],
                    marketToken = tokensResult[marketToken]
                )
            else null
        }
    }
}