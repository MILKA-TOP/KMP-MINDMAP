plugins {
    kotlin("android")
    id("com.android.library")
    id("org.jetbrains.compose")
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "ru.lipt.catalog"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
}

dependencies {
    implementation(Dependencies.Voyager.navigator)

    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material)
    @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
    implementation(compose.components.resources)
    implementation(project(":modules-app:navigation"))
}