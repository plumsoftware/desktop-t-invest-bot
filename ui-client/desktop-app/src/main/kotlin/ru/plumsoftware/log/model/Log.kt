package ru.plumsoftware.log.model

object Log {
    object Path {
        object Sandbox {
            private val user = System.getProperty("user.name")
            val folderName: String = "T-Invest bot"
            val mainPathToSandboxLogFolder: String =  "C:\\Users\\${user}\\AppData\\Local\\${folderName}\\sandbox\\log"
        }

        object Market {
            private val user = System.getProperty("user.name")
            val folderName: String = "T-Invest bot"
            val mainPathToMarketLogFolder: String =  "C:\\Users\\${user}\\AppData\\Local\\${folderName}\\market\\log"
        }
    }
}