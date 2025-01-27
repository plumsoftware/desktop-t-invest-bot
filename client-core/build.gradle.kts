plugins {
    id("io.ktor.plugin").version("2.3.12")
    application
    kotlin("jvm")
    kotlin("plugin.serialization").version(libs.versions.kotlin)
}

group = "ru.plumsoftware"
version = "0.0.1"

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

    //Test
    testImplementation(libs.kotlin.test.junit)

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