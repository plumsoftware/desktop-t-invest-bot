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

include(":server:trading-bot")
include(":shared-server-client:net-core")

include(":trading-core:t-trading")

include(":client:client-core")
include(":client:ui-client:telegram-bot")
include(":client:ui-client:desktop-app")
include(":client:ui-client:android-app")

include(":client:ui-core:theme-core")
include(":client:ui-core:components")
include(":client:ui-core:navigation")
