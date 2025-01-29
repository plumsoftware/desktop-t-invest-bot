plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "ru.plumsoftware"
version = "1.0-SNAPSHOT"

dependencies {
    //Auto generated
    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material)
    implementation(compose.ui)
    implementation(compose.components.resources)
    implementation(compose.components.uiToolingPreview)
    implementation(compose.desktop.currentOs)

    //Material
    implementation(libs.material3)

    //Test
    testImplementation(kotlin("test"))

    //Module
    implementation(project(":ui-core:theme-core"))
}