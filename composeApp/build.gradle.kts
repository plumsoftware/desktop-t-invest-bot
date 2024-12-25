import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.serialization)

    kotlin("jvm")
    id("org.jetbrains.compose")
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
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

    //Coroutines
    implementation(libs.kotlinx.coroutines)

    //Material
    implementation(libs.material3)

    //Json
    implementation(libs.serialization.json)

    //Precompose
    api(libs.precompose)
    api(libs.precompose.molecule)
    api(libs.precompose.viewmodel)

    //Invest api
    implementation("ru.tinkoff.piapi:java-sdk-core:1.27")

    //Test
    testImplementation(kotlin("test"))
}

compose.desktop {
    application {
        mainClass = "ru.plumsoftware.ui.presentation.MainKt"

        nativeDistributions {
            targetFormats(
                TargetFormat.Dmg, TargetFormat.Pkg, //MacOS
                TargetFormat.Msi, TargetFormat.Exe, //Windows
                TargetFormat.Deb, TargetFormat.Rpm //Linux
            )
            packageName = "ru.plumsoftware"
            packageVersion = "1.0.0"
        }
    }
}
