plugins {
    kotlin("android")
    id("com.android.library")
    id("org.jetbrains.compose")
}

android {
    namespace = "ru.lipt.navigation"
}

dependencies {
    implementation(Dependencies.Voyager.navigator)
}