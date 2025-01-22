plugins {
    kotlin("jvm")
}

group = "ru.plumsoftware"
version = "0.0.1"

dependencies {

    //Coroutines
    implementation(libs.kotlinx.coroutines)

    //Testing
    testImplementation(kotlin("test"))

    //Invest api
    implementation(libs.java.sdk.core)
}