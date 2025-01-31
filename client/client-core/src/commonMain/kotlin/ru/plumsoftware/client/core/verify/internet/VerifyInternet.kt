package ru.plumsoftware.client.core.verify.internet

import ru.plumsoftware.client.core.kmp_context.AppContext

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class VerifyInternet(context: AppContext) {

    fun isConnected(): Boolean
}
