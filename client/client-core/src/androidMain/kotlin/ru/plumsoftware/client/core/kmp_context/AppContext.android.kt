@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package ru.plumsoftware.client.core.kmp_context

import android.content.Context
import java.lang.ref.WeakReference

actual object AppContext {
    private var value: WeakReference<Context?>? = null

    fun set(context: Context) {
        value = WeakReference(context)
    }

    internal fun get(): Context? {
        return value?.get()
    }
}