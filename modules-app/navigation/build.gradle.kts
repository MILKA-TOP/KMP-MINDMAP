plugins {
    kotlin("android")
    id("com.android.library")
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "ru.lipt.navigation"
    kotlin {
        jvmToolchain(17)
    }
}

dependencies {
    implementation(Dependencies.Voyager.navigator)
}