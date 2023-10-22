import gradle.kotlin.dsl.accessors._9885c8525475a2a77e0b650bdf1e3c81.android

plugins {
    id("com.android.library")
}


android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
