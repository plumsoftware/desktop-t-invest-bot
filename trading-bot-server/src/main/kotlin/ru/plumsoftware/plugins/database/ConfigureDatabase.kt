package ru.plumsoftware.plugins.database

import io.ktor.server.application.Application
import org.jetbrains.exposed.sql.Database

fun Application.configureDatabase() {
    val config = environment.config
    val url = config.property("postgres.url").getString()
    val driver = config.property("postgres.driver").getString()
    val user = config.property("postgres.user").getString()
    val password = config.property("postgres.password").getString()
    Database.connect(url = url, driver = driver, user = user, password = password)
}