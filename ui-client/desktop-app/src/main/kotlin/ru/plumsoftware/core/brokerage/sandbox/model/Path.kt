package ru.plumsoftware.core.brokerage.sandbox.model

object Path {
    private val user = System.getProperty("user.name")
    val folderName: String = "T-Invest bot"
    val fileName: String = "sandbox_config.json"
    val mainPathToSandboxConfigFolder: String =  "C:\\Users\\${user}\\AppData\\Local\\$folderName"
    val mainPathToSandboxConfigFile: String = "C:\\Users\\${user}\\AppData\\Local\\$folderName\\$fileName"
}