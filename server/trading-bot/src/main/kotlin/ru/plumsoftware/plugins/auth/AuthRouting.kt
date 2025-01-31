package ru.plumsoftware.plugins.auth

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import ru.plumsoftware.facade.AuthFacade
import ru.plumsoftware.net.core.model.receive.PasswordMatchReceive
import ru.plumsoftware.net.core.model.receive.TTokensReceive
import ru.plumsoftware.net.core.model.receive.UserReceive
import ru.plumsoftware.net.core.model.response.UserResponseEither
import ru.plumsoftware.net.core.routing.AuthRouting
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
            put(path = AuthRouting.PUT_USER) {
                val userReceive = call.receive<UserReceive>()
                try {
                    val userResponse = authFacade.authNewUser(userReceive = userReceive)
                    call.respond(HttpStatusCode.OK, userResponse)
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        UserResponseEither.Error(msg = e.message ?: "")
                    )
                }
            }
            post(path = AuthRouting.POST_USER_T_TOKENS) {
                val tTokensReceive = call.receive<TTokensReceive>()

                try {
                    authFacade.insertTTokens(tTokensReceive = tTokensReceive)
                    call.respond(HttpStatusCode.OK)
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        UserResponseEither.Error(msg = e.message ?: "")
                    )
                }
            }
            get(path = AuthRouting.GET_USER_BY_PHONE) {
                val phone =
                    call.parameters["phone"] ?: throw IllegalArgumentException("Invalid phone")
                try {
                    when (val userResponseEither = authFacade.getUser(phone = phone)) {
                        is UserResponseEither.Error -> call.respond(HttpStatusCode.NotFound, userResponseEither)
                        is UserResponseEither.UserResponse -> call.respond(HttpStatusCode.OK, userResponseEither)
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.NotFound, e.message ?: "")
                }
            }
            get(path = AuthRouting.GET_T_TOKENS_BY_ID) {
                val id =
                    call.parameters["id"] ?: throw IllegalArgumentException("Invalid id")
                try {
                    val tTokensReceive = authFacade.getTTokens(id = id.toLong())
                    if (tTokensReceive != null)
                        call.respond(HttpStatusCode.OK, tTokensReceive)
                    else
                        call.respond(HttpStatusCode.BadRequest)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "")
                }
            }
            get(path = AuthRouting.GET_MATCH_PASSWORD) {
                val passwordMatchReceive = call.receive<PasswordMatchReceive>()

                val matches = authFacade.match(passwordMatchReceive = passwordMatchReceive)

                if (matches) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }
    }
}