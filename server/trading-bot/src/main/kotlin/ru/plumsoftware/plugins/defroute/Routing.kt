package ru.plumsoftware.plugins.defroute

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

fun Application.configureDefaultRouting() {
    routing {
        get("/{mode}") {
            val mode = call.parameters["mode"] ?: throw IllegalArgumentException("Illegal mode")
            when (mode) {
                "cbor" -> call.respond(HttpStatusCode.OK, HelloWorld())
            }
        }
    }
}

@Serializable
data class HelloWorld(
    @SerialName("hello_world") val hello: String = "Hello world!"
)
