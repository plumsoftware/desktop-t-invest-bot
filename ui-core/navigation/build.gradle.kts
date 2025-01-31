plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.library)
    id("org.jetbrains.compose")
}
kotlin {

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    androidTarget {
        publishLibraryVariants("release")
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }


    sourceSets {
        val commonMain by getting {
            dependencies {
                //Auto generated
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(compose.desktop.currentOs)

                //View model
                implementation(libs.lifecycle.viewmodel.compose)

                //Material
                implementation(libs.material3)

                //Precompose
                implementation(libs.precompose)

                //Client
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.ktor.client.cio)
                implementation(libs.logback.classic)
                implementation(libs.ktor.client.logging)
                implementation(libs.android.logging)
                implementation(libs.ktor.client.logging.jvm)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.auth)

                //Module
                implementation(project(":ui-core:theme-core"))
                implementation(project(":ui-core:components"))
                implementation(project(":client-core"))
                implementation(project(":net-core"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

android {
    namespace = "ru.plumsoftware"
    compileSdk = 35
    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    dependencies {}
}
dependencies {}
