package ru.plumsoftware.core.brokerage.mappers

import ru.tinkoff.piapi.contract.v1.InstrumentType

fun fromInstrumentTypeToName(instrumentType: InstrumentType): String {
    return when (instrumentType) {
        InstrumentType.INSTRUMENT_TYPE_UNSPECIFIED -> "Неопределён"
        InstrumentType.INSTRUMENT_TYPE_BOND -> "Облигация"
        InstrumentType.INSTRUMENT_TYPE_SHARE -> "Акция"
        InstrumentType.INSTRUMENT_TYPE_CURRENCY -> "Валюта"
        InstrumentType.INSTRUMENT_TYPE_ETF -> "Фонт"
        InstrumentType.INSTRUMENT_TYPE_FUTURES -> "Фьючерс"
        InstrumentType.INSTRUMENT_TYPE_SP -> "SP"
        InstrumentType.INSTRUMENT_TYPE_OPTION -> "Опцион"
        InstrumentType.INSTRUMENT_TYPE_CLEARING_CERTIFICATE -> "Чистящий сертификат"
        InstrumentType.INSTRUMENT_TYPE_INDEX -> "Индекс"
        InstrumentType.INSTRUMENT_TYPE_COMMODITY -> "Товар"
        InstrumentType.UNRECOGNIZED -> "Нераспознано"
    }
}

fun fromInstrumentTypeStrToRuStr(instrumentType: String): String {
    return when (instrumentType.lowercase()) {
        "share" -> "Акция"
        "bond" -> "Облигация"
        "currency" -> "Валюта"
        "futures" -> "Фьючерс"
        else -> "Неопределено"
    }
}