package ru.plumsoftware.core.settings.model

object Path {
    private val user = System.getProperty("user.name")
    val folderName: String = "T-Invest bot"
    val fileName: String = "config.json"
    val mainPathToSettingsFolder: String =  "C:\\Users\\${user}\\AppData\\Local\\$folderName"
    val mainPathToSettingsFile: String = "C:\\Users\\${user}\\AppData\\Local\\$folderName\\$fileName"
}