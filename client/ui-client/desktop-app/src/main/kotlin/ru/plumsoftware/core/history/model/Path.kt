package ru.plumsoftware.core.history.model

object Path {
    private val user = System.getProperty("user.name")
    val folderName: String = "T-Invest bot"
    val historyFolder: String = "history"
    val historySandboxFolder: String = "sandbox"
    val historyMarketFolder: String = "market"

    val mainPathToHistorySandboxFolder: String = "C:\\Users\\${user}\\AppData\\Local\\$folderName\\$historyFolder\\$historySandboxFolder"
    val mainPathToHistoryMarketFolder: String = "C:\\Users\\${user}\\AppData\\Local\\$folderName\\$historyFolder\\$historyMarketFolder"
}