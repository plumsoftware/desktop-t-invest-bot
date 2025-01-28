plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "ru.plumsoftware"
version = "1.0-SNAPSHOT"

compose.resources {
    publicResClass = true
    packageOfResClass = "ru.plumsoftware.ui.core.resources"
    generateResClass = auto

    customDirectory(
        sourceSetName = "main",
        directoryProvider = provider { layout.projectDirectory.dir("coreResources") }
    )
}

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
}