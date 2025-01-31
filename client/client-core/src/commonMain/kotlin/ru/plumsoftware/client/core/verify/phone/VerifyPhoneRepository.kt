package ru.plumsoftware.client.core.verify.phone

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class VerifyPhoneRepository() {
    fun sendCode(code: String, number: String)
}