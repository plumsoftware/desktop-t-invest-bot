package ru.plumsoftware.client.core.verify.internet

import ru.plumsoftware.client.core.kmp_context.AppContext
import java.net.InetAddress
import java.net.UnknownHostException

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class VerifyInternet actual constructor(context: AppContext) {
    actual fun isConnected(): Boolean {
        return try {
            val address = InetAddress.getByName("https://www.yandex.ru/")
            address != null && !address.equals("")
        } catch (e: UnknownHostException) {
            false
        }
    }
}