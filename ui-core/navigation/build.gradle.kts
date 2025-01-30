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

    //View model
    implementation(libs.lifecycle.viewmodel.compose)

    //Material
    implementation(libs.material3)

    //Precompose
    implementation(libs.precompose)

    //Test
    testImplementation(kotlin("test"))

    //Module
    implementation(project(":ui-core:theme-core"))
    implementation(project(":ui-core:components"))
    implementation(project(":client-core"))
    implementation(project(":net-core"))
}