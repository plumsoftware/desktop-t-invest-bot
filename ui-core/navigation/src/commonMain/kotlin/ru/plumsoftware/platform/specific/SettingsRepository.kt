package ru.plumsoftware.platform.specific

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class SettingsRepository(vararg params: Any?) {
    suspend fun saveUserId(id: Long)
}