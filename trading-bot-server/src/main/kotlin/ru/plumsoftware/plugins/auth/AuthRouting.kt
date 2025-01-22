package ru.plumsoftware.plugins.auth

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import ru.plumsoftware.model.UserDto
import ru.plumsoftware.service.auth.AuthService

fun Application.configureAuthRouting() {
    val authService = AuthService()

    routing {
        post(path = "user") {
            val userDto = call.receive<UserDto>()
            try {
                authService.createNewUser(userDto = userDto)
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "")
            }
        }
        get(path = "user/{phone}") {
            val phone = call.parameters["phone"] ?: throw IllegalArgumentException("Invalid phone")
            try {
                val userDto = authService.getUserByPhone(phone = phone)
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