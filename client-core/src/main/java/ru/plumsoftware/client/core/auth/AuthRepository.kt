package ru.plumsoftware.client.core.auth

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.appendPathSegments
import ru.plumsoftware.net.core.model.receive.PasswordMatchReceive
import ru.plumsoftware.net.core.model.receive.TTokensReceive
import ru.plumsoftware.net.core.model.receive.UserReceive
import ru.plumsoftware.net.core.model.response.UserResponseEither
import ru.plumsoftware.net.core.routing.AuthRouting

class AuthRepository(private val client: HttpClient, private val baseUrl: String) {

    suspend fun putUser(userReceive: UserReceive): UserResponseEither {
        val response: HttpResponse = client.put(urlString = baseUrl) {
            url {
                appendPathSegments(AuthRouting.PUT_USER)
            }
            setBody(userReceive)
        }

        return if (response.status.value in 200..299) {
            response.body<UserResponseEither.UserResponse>()
        } else {
            response.body<UserResponseEither.Error>()
        }
    }

    suspend fun postTTokens(tTokensReceive: TTokensReceive): HttpStatusCode {
        val response = client.post(urlString = baseUrl) {
            url {
                appendPathSegments(AuthRouting.POST_USER_T_TOKENS)
            }
            setBody(tTokensReceive)
        }

        return response.status
    }

    suspend fun getUserByPhone(phone: String): UserReceive? {
        val response = client.get(urlString = baseUrl) {
            url {
                appendPathSegments(AuthRouting.GET_USER_BY_PHONE)
                parameters.append("phone", phone)
            }
        }

        return if (response.status.value in 200..299)
            response.body<UserReceive>()
        else null
    }

    suspend fun getTTokensByUserId(id: Long): TTokensReceive? {
        val response = client.get(urlString = baseUrl) {
            url {
                appendPathSegments(AuthRouting.GET_T_TOKENS_BY_ID)
                parameters.append("id", id.toString())
            }
        }
        return if (response.status.value in 200..299)
            response.body<TTokensReceive>()
        else null
    }

    suspend fun getPasswordMatch(passwordMatchReceive: PasswordMatchReceive): HttpStatusCode {
        val response = client.get(urlString = baseUrl) {
            url {
                appendPathSegments(AuthRouting.GET_MATCH_PASSWORD)
            }
            setBody(passwordMatchReceive)
        }
        return response.status
    }
}