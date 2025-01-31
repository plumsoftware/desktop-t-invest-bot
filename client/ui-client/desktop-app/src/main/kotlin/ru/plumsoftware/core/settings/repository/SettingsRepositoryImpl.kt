package ru.plumsoftware.core.settings.repository

import kotlinx.serialization.json.Json
import ru.plumsoftware.core.settings.model.ApiTokens
import ru.plumsoftware.core.settings.model.Path
import ru.plumsoftware.core.settings.model.Settings
import java.io.File

class SettingsRepositoryImpl : SettingsRepository {

    init {
        createFileEndPointIfNotExists()
    }

    override suspend fun saveTokens(apiTokens: ApiTokens) {
        val file = File(Path.mainPathToSettingsFile)

        val settings = Json.decodeFromString<Settings>(file.readText())
        val encoded = encode(settings.copy(apiTokens = apiTokens))

        file.writeText(text = encoded)
    }

    override suspend fun saveIsDark(isDark: Boolean) {
        val file = File(Path.mainPathToSettingsFile)

        val settings = Json.decodeFromString<Settings>(file.readText())
        val encoded = encode(settings.copy(isDark = isDark))

        file.writeBytes(encoded.encodeToByteArray())

    }

    override suspend fun saveSettings(settings: Settings) {
        val file = File(Path.mainPathToSettingsFile)
        val encoded = encode(settings)
        file.writeBytes(encoded.encodeToByteArray())
    }

    override suspend fun getSettings(): Settings {
        val file = File(Path.mainPathToSettingsFile)
        val text = file.readText()
        val settings = Json.decodeFromString<Settings>(text)
        return settings
    }

    private fun createFileEndPointIfNotExists() {
        val folder = File(Path.mainPathToSettingsFolder)

        if (!folder.exists()) {
            val mkdir: Boolean = folder.mkdir()

            if (mkdir) {
                val file = File(Path.mainPathToSettingsFile)
                if (!file.exists()) {
                    file.createNewFile()
                    file.writeBytes(encode(Settings()).encodeToByteArray())
                }
            }
        } else {
            val file = File(Path.mainPathToSettingsFile)
            if (!file.exists()) {
                file.createNewFile()
                file.writeBytes(encode(Settings()).encodeToByteArray())
            }
        }
    }

    private fun encode(settings: Settings): String {
        val strBuilder = StringBuilder()
        strBuilder
            .append("{\n")
            .append("    \"is_dark\": ${settings.isDark},\n")
            .append("    \"api_tokens\": {\n")
            .append("        \"sandbox_token\": \"${settings.apiTokens.sandboxToken}\",\n")
            .append("        \"token\": \"${settings.apiTokens.token}\"\n")
            .append("    }\n")
            .append("}")
        return strBuilder.toString()
    }
}