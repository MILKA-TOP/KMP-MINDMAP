plugins {
    kotlin("android")
    id("com.android.library")
    id("org.jetbrains.compose")
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "ru.lipt.map"
}

dependencies {
    implementation(Dependencies.Voyager.navigator)

    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material)
    @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
    implementation(compose.components.resources)
}