plugins {
    id("io.ktor.plugin").version("2.3.11")
    application
    kotlin("jvm")
    kotlin("plugin.serialization").version(libs.versions.kotlin)
}

group = "ru.plumsoftware"
version = "0.0.1"

dependencies {

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${libs.versions.kotlin}")
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.netty.jvm)
    implementation(libs.ktor.server.core.jvm)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    testImplementation(libs.ktor.server.test.host.jvm)
    testImplementation(libs.kotlin.test.junit)

    //Serialization
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.serialization.kotlinx.cbor)
    implementation(libs.ktor.serialization.kotlinx.xml)

    //Json
    implementation(libs.serialization.json)
    implementation(libs.serialization.json.jvm)
}