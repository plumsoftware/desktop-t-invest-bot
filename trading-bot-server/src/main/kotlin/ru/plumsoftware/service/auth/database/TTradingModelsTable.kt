package ru.plumsoftware.service.auth.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import ru.plumsoftware.net.core.model.dto.trading.TradingModelDto
import ru.plumsoftware.net.core.model.dto.trading.TradingModelsDto
import ru.plumsoftware.net.core.model.mode.StartsWithMode
import ru.plumsoftware.net.core.model.mode.TradingMode

object TTradingModelsTable : Table("t_trading_models") {
    private val tradingModelId = TTradingModelsTable.long("trading_model_id")
    private val id = TTradingModelsTable.long("id")
    private val figi = TTradingModelsTable.text("figi")
    private val lots = TTradingModelsTable.integer("lots")
    private val tick = TTradingModelsTable.long("tick")
    private val startsWith = TTradingModelsTable.text("starts_with")
    private val tradingMode = TTradingModelsTable.text("trading_mode")
    private val percentIncrease = TTradingModelsTable.float("percent_increase")
    private val percentDecrease = TTradingModelsTable.float("percent_decrease")

    suspend fun insert(tradingModelsDto: TradingModelsDto) {
        tradingModelsDto.tradingModelsDto.forEach { tradingModelDto ->
            dbQuery {
                TTradingModelsTable.insert {
                    it[this.tradingModelId] = tradingModelDto.tradingModelId
                    it[this.id] = tradingModelsDto.id
                    it[this.figi] = tradingModelDto.figi
                    it[this.lots] = tradingModelDto.lots
                    it[this.tick] = tradingModelDto.tick
                    it[this.startsWith] = tradingModelDto.startsWith.name
                    it[this.tradingMode] = tradingModelDto.tradingMode.name
                    it[this.percentIncrease] = tradingModelDto.percentIncrease
                    it[this.percentDecrease] = tradingModelDto.percentDecrease
                }
            }
        }
    }

    suspend fun get(id: Long): TradingModelsDto {
        return dbQueryReturn {
            val tradingModel = TTradingModelsTable
                .selectAll()
                .where { TTradingModelsTable.id.eq(id) }
                .map {
                    TradingModelDto(
                        tradingModelId = it[this.tradingModelId],
                        figi = it[this.figi],
                        lots = it[this.lots],
                        tick = it[this.tick],
                        startsWith = StartsWithMode.fromString(it[this.startsWith])
                            ?: StartsWithMode.BUY,
                        tradingMode = TradingMode.fromString(it[this.startsWith])
                            ?: TradingMode.MARKET,
                        percentIncrease = it[this.percentIncrease],
                        percentDecrease = it[this.percentDecrease]
                    )
                }

            TradingModelsDto(
                id = id,
                tradingModelsDto = tradingModel
            )
        }
    }
}