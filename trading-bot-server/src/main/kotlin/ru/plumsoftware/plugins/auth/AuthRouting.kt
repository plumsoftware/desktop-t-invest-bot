package ru.plumsoftware.plugins.auth

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import ru.plumsoftware.facade.AuthFacade
import ru.plumsoftware.model.receive.UserReceive
import ru.plumsoftware.service.auth.AuthService
import service.cryptography.CryptographyService
import service.hash.HashService

fun Application.configureAuthRouting() {
    val applicationConfig = environment.config
    val authService = AuthService()
    val hashService = HashService(applicationConfig)
    val cryptographyService =
        CryptographyService(applicationConfig)
    val authFacade = AuthFacade(
        authService = authService,
        hashService = hashService,
        cryptographyService = cryptographyService
    )

    routing {
        authenticate(AuthConfig.BEARER_AUTH) {
            post(path = "user") {
                val userReceive = call.receive<UserReceive>()
                try {
                    authFacade.authNewUser(userReceive = userReceive)
                    call.respond(HttpStatusCode.OK)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "")
                }
            }
            get(path = "user/{phone}") {
                val phone =
                    call.parameters["phone"] ?: throw IllegalArgumentException("Invalid phone")
                try {
                    val userDto = authFacade.getUser(phone = phone)
                    if (userDto != null)
                        call.respond(HttpStatusCode.OK, userDto)
                    else
                        call.respond(HttpStatusCode.NotFound)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.NotFound, e.message ?: "")
                }
            }
        }
    }
}