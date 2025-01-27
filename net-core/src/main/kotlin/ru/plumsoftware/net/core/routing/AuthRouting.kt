package ru.plumsoftware.net.core.routing

object AuthRouting {
    const val PUT_USER = "user"
    const val POST_USER_T_TOKENS = "user/t/tokens"
    const val GET_USER_BY_PHONE = "user/{phone}"
    const val GET_T_TOKENS_BY_ID = "t/tokens/{id}"
    const val GET_MATCH_PASSWORD = "t/password/match"
}