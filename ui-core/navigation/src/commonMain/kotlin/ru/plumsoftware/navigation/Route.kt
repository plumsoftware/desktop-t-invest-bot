package ru.plumsoftware.navigation

object Route {

    object Group {
        const val AUTH = "/auth"
        const val MAIN = "/main"
    }

    object Auth {
        const val AUTH_VARIANT = "/auth_variant"
        const val REGISTER = "/register"
        const val AUTHENTICATION = "/authentication"
        const val CONFIRM_NUMBER = "/confirm_number"
        const val PRIVACY_POLICY = "/privacy_policy"
    }

    object Main {
        const val HOME = "/home"
    }
}