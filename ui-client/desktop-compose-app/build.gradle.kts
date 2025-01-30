import org.jetbrains.compose.desktop.application.dsl.TargetFormat

val macExtraPlistKeys: String
    get() = """
      <key>CFBundleURLTypes</key>
      <array>
        <dict>
          <key>CFBundleURLName</key>
          <string>Example deep link</string>
          <key>CFBundleURLSchemes</key>
          <array>
            <string>compose</string>
          </array>
        </dict>
      </array>
    """

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization").version(libs.versions.kotlin)
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

    //Coroutines
    implementation(libs.kotlinx.coroutines)

    //Material
    implementation(libs.material3)

    //Json
    implementation(libs.serialization.json)
    implementation(libs.serialization.json.jvm)

    //Cbor
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.6.3")

    //Precompose
    api(libs.precompose)
    api(libs.precompose.molecule)
    api(libs.precompose.viewmodel)

    //Invest api
    implementation("ru.tinkoff.piapi:java-sdk-core:1.27")

    //Test
    testImplementation(kotlin("test"))

    //Module
    implementation(project(path = ":ui-core:navigation"))
    implementation(project(path = ":net-core"))
    implementation(project(path = ":ui-core:theme-core"))
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
            packageName = "Торговый робот"
            packageVersion = "1.0.0"

            description =
                "Это программа для торговли с использованием Т инвестиции. Разработчик: Владелец этого аккаунта."
            copyright = "© 2024 владелец этого аккаунта All rights reserved."
            vendor = "plumsoftware"
            licenseFile.set(project.file("LICENSE.txt"))

            windows {
                packageVersion = "1.0.0"
                msiPackageVersion = "1.0.0"
                exePackageVersion = "1.0.0"
                iconFile.set(project.file("logo.ico"))
            }
            macOS {
                packageVersion = "1.0.0"
                dmgPackageVersion = "1.0.0"
                pkgPackageVersion = "1.0.0"
                packageBuildVersion = "1.0.0"
                dmgPackageBuildVersion = "1.0.0"
                pkgPackageBuildVersion = "1.0.0"
                iconFile.set(project.file("logo.icns"))

                bundleID = "ru.plumsoftware"
                infoPlist {
                    extraKeysRawXml = macExtraPlistKeys
                }
            }
            linux {
                packageVersion = "1.0.0"
                debPackageVersion = "1.0.0"
                rpmPackageVersion = "1.0.0"
                iconFile.set(project.file("logo.png"))
            }
        }
    }
}