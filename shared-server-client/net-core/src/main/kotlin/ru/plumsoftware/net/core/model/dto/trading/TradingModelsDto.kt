package ru.plumsoftware.net.core.model.dto.trading

data class TradingModelsDto(
    val id: Long,
    val tradingModelsDto: List<TradingModelDto>
) {
    companion object {
        fun empty(): TradingModelsDto = TradingModelsDto(
            id = -1L,
            tradingModelsDto = emptyList()
        )
    }
}