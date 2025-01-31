package ru.plumsoftware.service.statistic.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import ru.plumsoftware.net.core.model.dto.StatisticDto
import ru.plumsoftware.service.auth.database.dbQuery
import ru.plumsoftware.service.auth.database.dbQueryReturn

object StatisticSandboxTable : Table("statistic_sandbox_table") {
    private val id = StatisticSandboxTable.long("id").autoIncrement()
    private val userId = StatisticSandboxTable.long("user_id")
    private val minute = StatisticSandboxTable.integer("minute")
    private val hour = StatisticSandboxTable.integer("hour")
    private val day = StatisticSandboxTable.integer("day")
    private val month = StatisticSandboxTable.integer("month")
    private val year = StatisticSandboxTable.integer("year")
    private val startTradingValue = StatisticSandboxTable.text("start_trading_value")
    private val stopTradingValue = StatisticSandboxTable.text("stop_trading_value")

    suspend fun insert(statisticDto: StatisticDto) {
        dbQuery {
            StatisticSandboxTable.insert {
                it[this.userId] = statisticDto.userId
                it[this.minute] = statisticDto.minute
                it[this.hour] = statisticDto.hour
                it[this.day] = statisticDto.day
                it[this.month] = statisticDto.month
                it[this.year] = statisticDto.year
                it[this.startTradingValue] = statisticDto.startTradingValue
                it[this.stopTradingValue] = statisticDto.stopTradingValue
            }
        }
    }

    suspend fun get(id: Long): List<StatisticDto> {
        return dbQueryReturn {
            return@dbQueryReturn StatisticSandboxTable
                .selectAll()
                .where { userId.eq(id) }
                .map {
                    StatisticDto(
                        id = it[this.id],
                        userId = it[this.userId],
                        minute = it[this.minute],
                        hour = it[this.hour],
                        day = it[this.day],
                        month = it[this.month],
                        year = it[this.year],
                        startTradingValue = it[this.startTradingValue],
                        stopTradingValue = it[this.stopTradingValue]
                    )
                }
        }
    }

    suspend fun update(id: Long, stopTradingValue: String) {
        dbQuery {
            StatisticSandboxTable.update({ StatisticSandboxTable.id.eq(id) }) {
                it[this.stopTradingValue] = stopTradingValue
            }
        }
    }
}