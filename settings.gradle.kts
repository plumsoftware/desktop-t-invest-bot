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

include(":trading-bot-server")
include(":net-core")

include(":trading-core:t-trading")

include(":client-core")
include(":ui-client:telegram-bot")
include(":ui-client:desktop-app")
include(":ui-client:android-app")

include(":ui-core:theme-core")
include(":ui-core:components")
include(":ui-core:navigation")
