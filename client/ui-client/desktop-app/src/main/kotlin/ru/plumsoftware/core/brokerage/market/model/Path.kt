package ru.plumsoftware.core.brokerage.market.model

object Path {
    private val user = System.getProperty("user.name")
    val folderName: String = "T-Invest bot"
    val fileName: String = "market_config.json"
    val mainPathToMarketConfigFolder: String =  "C:\\Users\\${user}\\AppData\\Local\\$folderName"
    val mainPathToMarketConfigFile: String = "C:\\Users\\${user}\\AppData\\Local\\$folderName\\$fileName"
}