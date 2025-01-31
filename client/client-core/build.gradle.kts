plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.library)
//    id("io.ktor.plugin").version("2.3.12")
    kotlin("plugin.serialization").version(libs.versions.kotlin)
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

                //Serialization
                implementation(libs.ktor.server.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.serialization.kotlinx.cbor)
                implementation(libs.ktor.serialization.kotlinx.xml)

                //Json
                implementation(libs.serialization.json)
                implementation(libs.serialization.json.jvm)

                //Module
                implementation(project(path = ":net-core"))
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