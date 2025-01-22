package ru.plumsoftware.plugins.serialization

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.application.Application
import io.ktor.server.application.install
import kotlinx.serialization.json.Json

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            prettyPrint = true
        })
    }
}