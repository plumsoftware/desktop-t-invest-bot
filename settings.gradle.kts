rootProject.name = "t-invest-bot"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

include(":desktop-compose-app")
include(":android-app")
include(":trading-bot-server")
include(":net-core")
include(":trading-core:t-trading")
include(":client-core-core")
include(":ui-core:theme-core")
include(":telegram-bot")
include(":ui-core:components")
