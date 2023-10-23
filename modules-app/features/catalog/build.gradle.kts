plugins {
    kotlin("android")
    id("com.android.library")
    id("org.jetbrains.compose")
}

android {
    namespace = "ru.lipt.catalog"
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