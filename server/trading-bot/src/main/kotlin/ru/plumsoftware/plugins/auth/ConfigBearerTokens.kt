package ru.plumsoftware.plugins.auth

import io.ktor.server.application.*
import io.ktor.server.auth.BearerTokenCredential
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authentication
import io.ktor.server.auth.bearer

fun Application.configBearerAuth() {
    val token = environment.config.property("token").getString()
    authentication {
        bearer(AuthConfig.BEARER_AUTH) {
            realm = "Access to the '/' path"
            authenticate { bearerTokenCredential: BearerTokenCredential ->
                if (bearerTokenCredential.token == token) {
                    UserIdPrincipal("trading-bot")
                } else null
            }
        }
    }
}